package com.example.howdy

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.example.howdy.databinding.ActivityConfiguracaoBinding
import com.example.howdy.model.MySqlResult
import com.example.howdy.model.UserTypes.User
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.convertStringtoEditable
import com.example.howdy.view.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import convertBrStringToDate
import convertDateToBackendFormat
import convertDateToBrString
import kotlinx.android.synthetic.main.activity_configuracao.*
import kotlinx.android.synthetic.main.dialog_view.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


const val CODE_BACKGROUND_IMAGE = 100
const val CODE_PROFILE_PHOTO = 110

class ConfiguracaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracaoBinding
    private val context = this
    var imageBitmap : Bitmap? = null

    private lateinit var routerInterface: RouterInterface
    private val auth = FirebaseAuth.getInstance()

    private lateinit var userLogged: User
    private var changedProfilePhoto: File? = null
    private var changedBackgroundImage: File? = null
    private var backgroundImageFormData: MultipartBody.Part? = null
    private var profilePhotoFormData: MultipartBody.Part? = null
    private lateinit var birthDateStringBackend: String
    private lateinit var birthDateDate: Date

    private lateinit var nativeLanguageNameView: AutoCompleteTextView
    private lateinit var targetLanguageNameView: AutoCompleteTextView
    private lateinit var profilePhotoView: ImageView
    private lateinit var backgroundImageView: ImageView
    private lateinit var ibChangeBackgroundImageView: ImageButton
    private lateinit var ibChangeProfilePhotoView: ImageButton
    private lateinit var btnDelteAccountView: Button
    private lateinit var userNameView: TextInputEditText
    private lateinit var descriptionView: TextInputEditText
    private lateinit var birthDateView: TextInputEditText
    private lateinit var emailView: TextInputEditText
    private lateinit var passwordView: TextInputEditText
    private lateinit var passwordConfirmationView: TextInputEditText
    private lateinit var buttonToEdit: Button

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        binding = ActivityConfiguracaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arrowButton = arrow_button

        nativeLanguageNameView = binding.selectedIdiomaNativo
        targetLanguageNameView = binding.selectedIdiomaInteresse
        userNameView = textNome
        descriptionView = textDescricao
        birthDateView = textData
        emailView = textEmail
        passwordView = textSenha
        passwordConfirmationView = textSenhaConfirmar
        buttonToEdit = buttonAlterar
        btnDelteAccountView = btnDeleteAccount
        profilePhotoView = userImage
        backgroundImageView = userBackground
        ibChangeProfilePhotoView = tvt_trocar_foto_user
        ibChangeBackgroundImageView = tvt_trocar_foto

        routerInterface = APIUtil.`interface`
        findAndRenderUserData()
        setOptionsToChangePhoto()

        //BOTÃO CORRESPONDENTE A SETA DE VOLTAR
        arrowButton.setOnClickListener{ finish() }

        buttonToEdit.setOnClickListener{ editUser()}

        btnDelteAccountView.setOnClickListener{ openDeleteUserModal()}
    }

    private fun editUser(){
        val userName = userNameView.text.toString()
        val description = descriptionView.text.toString()
        val birthDate = birthDateView.text.toString()
        birthDateDate = convertBrStringToDate(birthDate)
        birthDateStringBackend = convertDateToBackendFormat(birthDateDate)
        val email = emailView.text.toString()
        val password = passwordView.text.toString()
        val passwordConfirmation = passwordConfirmationView.text.toString()
        val nativeLanguageName = nativeLanguageNameView.text.toString()
        val targetLanguageName = targetLanguageNameView.text.toString()

        //FUNÇÃO QUE FARÁ A VALIDAÇÃO DE CAMPOS
        if (validateFields(userName, description, birthDate, email, password, passwordConfirmation, nativeLanguageName, targetLanguageName)){
            updateUser(
                userName,
                description,
                birthDateStringBackend,
                email,
                password,
                nativeLanguageName
            )
        }
    }

    //CRIANDO FUNÇÃO QUERY PARA ATUALIZAR USUÁRIO
    private fun updateUser(
        userName: String,
        description: String,
        birthDate: String,
        email: String,
        password: String,
        nativeLanguageName: String
    ){
        //DEFININDO QUAIS SERÃO OS OBJETOS DE TARGET E NATIVE LANGUAGE
        val idTargetLanguage: Int?
        val idNativeLanguage: Int?

        if (nativeLanguageName == "Português brasileiro"){
            idNativeLanguage = 1
            idTargetLanguage = 2
        } else {
            idNativeLanguage = 2
            idTargetLanguage = 1
        }

        //CONVERTENDO ARQUIVOS DE FOTO DE PERFIL, E FOTO DE FUNDO PARA MultipartBody.Part
            if(changedBackgroundImage != null){
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), changedBackgroundImage)
                backgroundImageFormData = MultipartBody.Part.createFormData("backgroundImageFile", changedBackgroundImage!!.name, requestFile)
            }
            if (changedProfilePhoto != null){
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), changedProfilePhoto)
                profilePhotoFormData = MultipartBody.Part.createFormData("profilePhotoFile", changedProfilePhoto!!.name, requestFile)
            }

        //LOGANDO O USUÁRIO COM SUA CONTA DO FIREBASE
        if (auth?.currentUser?.email != null) {
            auth.signInWithEmailAndPassword(
                auth!!.currentUser!!.email!!,
                textSenhaAtual.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    //ALTERANDO E-MAIL DO USUÁRIO ATRAVÉS DO FIREBASE
                    if (auth.currentUser?.email != email) {
                        changeEmail(email)
                    }

                    //ALTERANDO SENHA DO USUÁRIO ATRAVÉS DO FIREBASE
                    if (password != "") {
                        changePassword(password)
                    }
                }else{
                    Toast.makeText(applicationContext,"A senha que foi passada está incorreta", Toast.LENGTH_LONG).show()
                }
            }
        }

        //RESGATANDO IDTOKEN DO FIREBASE
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!

                //CONVERTENDO TEXTOS DA REQUISIÇÃO EM REQUESTBODY
                val idTargetLanguageRequestBody = RequestBody.create(MediaType.parse("text/plain"), idTargetLanguage.toString())
                val idNativeLanguageRequestBody = RequestBody.create(MediaType.parse("text/plain"), idNativeLanguage.toString())
                val userNameRequestBody = RequestBody.create(MediaType.parse("text/plain"), userName)
                val birthDateRequestBody = RequestBody.create(MediaType.parse("text/plain"), birthDate)
                val descriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), description)

                //REALIZANDO A QUERY PARA ATUALIZAR USUÁRIO
                    val call: Call<MySqlResult> = routerInterface.editMyAccount(
                        idToken,
                        profilePhotoFormData,
                        backgroundImageFormData,
                        idTargetLanguageRequestBody,
                        idNativeLanguageRequestBody,
                        userNameRequestBody,
                        birthDateRequestBody,
                        descriptionRequestBody,
                    )

                call.enqueue(object : Callback<MySqlResult> {
                    override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Usuário atualizado com sucesso!", Toast.LENGTH_LONG).show()

                            //INDO PARA A TELA PRINCIPAL DA APLICAÇÃO, PARA TER OS DADOS DO USUÁRIO ATUALIZADOS
                            restartApp()
                        } else {
                            Toast.makeText(
                                context, "Ocorreu um erro na edição, certifique-se de que os campos foram preenchidos corretamente.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                        Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NA TRADUÇÃO: $t")
                    }
                })
                }
            }

    private fun changeEmail(email: String){
        auth.currentUser?.updateEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "E-mail alterado com sucesso!",
                        Toast.LENGTH_SHORT).show()
                } else {
                    //IMPRIMINDO A MENSAGEM DE ERRO DA REQUISIÇÃO
                    println("DEBUGANDO ERRO AO ALTERAR E-MAIL" + task.exception)

                    Toast.makeText(
                        this,
                        "Erro ao atualizar e-mail!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun changePassword(password: String){
        auth.currentUser?.updatePassword(password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Senha alterada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    restartApp()
                } else {
                    println("DEBUGANDO ERRO AO ALTERAR SENHA" + task.exception)

                    Toast.makeText(
                        this,
                        "Erro ao atualizar senha",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //FUNÇÃO QUE REINICIA O APLICATIVO
    private fun restartApp() {
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finishAffinity()
    }

    //CRIANDO FUNÇÃO QUE VALIDA OS CAMPOS
    private fun validateFields(userName: String, description: String, birthDate: String, email: String, password: String, passwordConfirmation: String, nativeLanguageName: String, targetLanguageName: String): Boolean{
        var isValid = true
        if (userName.isEmpty()){
            userNameView.error = "Preencha o campo nome"
            isValid = false
        }
        if (description.isEmpty()){
            descriptionView.error = "Preencha o campo nome"
            isValid = false
        }
        if (birthDate.isEmpty()){
            birthDateView.error = "Preencha o campo data de nascimento"
            isValid = false
        }
        if (email.isEmpty()){
            emailView.error = "Preencha o campo email"
            isValid = false
        }
        if (!password.isEmpty() && password.length < 6){
            passwordView.error = "A senha deve ter no mínimo 6 caracteres"
            isValid = false
        }
        if (password != passwordConfirmation){
            passwordConfirmationView.error = "As senhas não conferem"
            isValid = false
        }
        if (nativeLanguageName == targetLanguageName){
            targetLanguageNameView.error = "O idioma de interesse não pode ser o mesmo do idioma de origem"
            isValid = false
        }

        return isValid
    }

    private fun openDeleteUserModal(){
        val view = View.inflate(this, R.layout.dialog_view, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        view.bnt_cancelar.setOnClickListener {
            dialog.dismiss()
        }

        view.bnt_excluir.setOnClickListener {
            deleteUser()

            dialog.dismiss()
        }
    }

    private fun deleteUser(){
        /** DELETANDO USUÁRIO **/

        if(textSenhaAtual.text.toString() != ""){
            //RENOVANDO SESSÃO
            auth.signInWithEmailAndPassword(
                auth!!.currentUser!!.email!!,
                textSenhaAtual.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    //DELETANDO NO BANCO DE DADOS

                    //RESGATANDO IDTOKEN DO FIREBASE
                    auth.currentUser?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            val idToken = result.token!!

                            //REALIZANDO A QUERY PARA DELETAR USUÁRIO
                            val call: Call<MySqlResult> = routerInterface.deleteMyAccountOnBackend(idToken)

                            call.enqueue(object : Callback<MySqlResult> {
                                override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                                    if (response.isSuccessful) {
                                        deleteAccountOnFirebase()
                                    } else {
                                        Toast.makeText(
                                            context, "Ocorreu um erro em deletar sua conta, tente novamente.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                                    Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                                        Toast.LENGTH_LONG).show()
                                    println("DEBUGANDO - ONFAILURE NA DELEÇÃO DE CONTA: $t")
                                }
                            })
                        }
                }else{
                    Toast.makeText(applicationContext,"A senha que foi passada está incorreta", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            //DIZENDO AO USUÁRIO QUE A SENHA DEVE SER PREENCHIDA

                Toast.makeText(this, "Escreva sua senha para deletar esta conta.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAccountOnFirebase(){
        //DELETANDO CONTA DO USUÁRIO UTILIZANDO O FIREBASE
        val user = auth.currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Usuário excluído com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    restartApp()
                } else {
                    println("DEBUGANDO ERRO AO EXCLUIR USUÁRIO" + task.exception)

                    Toast.makeText(
                        this,
                        "Erro ao excluir usuário",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setOptionsToChangePhoto(){
        /** TRATANDO IMAGENS **/
        //Caso a imagem seja um vector asset
        imageBitmap =
            resources
                .getDrawable(R.drawable.ic_pessoa)
                .toBitmap()

        ibChangeProfilePhotoView.setOnClickListener {
            openGallery(CODE_PROFILE_PHOTO)
        }

        ibChangeBackgroundImageView.setOnClickListener {
            openGallery(CODE_BACKGROUND_IMAGE)
        }
    }

    private fun findAndRenderUserData(){
        //COLETANDO INFORMAÇÕES ATUAIS DO USUÁRIO LOGADO, PARA ENTÃO PREENCHER NO INPUT
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token
                if (idToken != null) {
                    val call: Call<List<User>> = routerInterface.isMyUidExternalRegistered(idToken)
                    call.enqueue(object : Callback<List<User>> {
                        override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                            if (response.isSuccessful) {
                                userLogged = response.body()?.get(0)!!
                                renderUserData()
                            } else {
                                Toast.makeText(
                                    context, "OPS... OCORREU UM ERRO AO RESGATAR OS DADOS DO USUÁRIO!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<List<User>>, t: Throwable) {
                            Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                                Toast.LENGTH_LONG).show()
                            println("DEBUGANDO - ONFAILURE NA EDIÇÃO DO USUÁRIO $t")
                        }
                    })
                }
            }
    }

    @SuppressLint("SetTextI18n")
    fun renderUserData(){
        //BUSCANDO A FOTO DE PERFIL DO USUÁRIO ATRAVÉS DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
        if(userLogged.profilePhoto != null) {
            Glide
                .with(profilePhotoView)
                .load(userLogged.profilePhoto)
                .into(profilePhotoView)
        }

        //BUSCANDO FOTO DE CAPA DO USUÁRIO
        if(userLogged.backgroundImage != null) {
            Glide
                .with(backgroundImageView)
                .load(userLogged.backgroundImage)
                .into(backgroundImageView)
        }

        userNameView.text = convertStringtoEditable(userLogged.userName)
        descriptionView.text = convertStringtoEditable(userLogged.description!!)
        birthDateView.text = convertStringtoEditable(convertDateToBrString(userLogged.birthDate))
        emailView.text = convertStringtoEditable(auth?.currentUser?.email + "")

        //RESGATANDO ANO DE NASCIMENTO DO USUÁRIO
        val birthYear = userLogged.birthDate.toString().substring(30, 34).toInt()
        //RESGATANDO MÊS DE NASCIMENTO DO USUÁRIO
        val birthMonth = userLogged.birthDate.month
        //RESGATANDO DIA DE NASCIMENTO DO USUÁRIO
        val birthDay = userLogged.birthDate.toString().substring(8,10).toInt()
        birthDateView.setOnClickListener {
            val data = DatePickerDialog(
                this, { _, _year, _month, _day ->
                    birthDateView.setText("$_day/${_month + 1}/$_year")
                }, birthYear, birthMonth, birthDay)
            data.show()
        }

        /** LISTANDO IDIOMA DE INTERESSE, NATIVO, E SELECIONANDO A QUE JÁ É CORRESPONDENTE A DO USUÁRIO **/
        val countries = resources.getStringArray(R.array.linguagens)

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            countries
        )

        nativeLanguageNameView.text = convertStringtoEditable(userLogged.nativeLanguageName)
        with(nativeLanguageNameView){
            setAdapter(adapter)
        }

        targetLanguageNameView.text = convertStringtoEditable(userLogged.targetLanguageName)
        with(targetLanguageNameView){
            setAdapter(adapter)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imagem: Intent?) {
        super.onActivityResult(requestCode, resultCode, imagem)

        if (requestCode == CODE_BACKGROUND_IMAGE && resultCode == -1 || requestCode == CODE_PROFILE_PHOTO && resultCode == -1) {
            //Recuperar a imagem do stream(fluxo)
            val fluxoImagem = contentResolver.openInputStream(imagem!!.data!!)

            //Converter os bits em um bitmap
            imageBitmap = BitmapFactory.decodeStream(fluxoImagem)

            //SUBSTITUINDO CONTEÚDOS DE IMAGE VIEW PELAS RESPECTIVAS IMAGENS
            if (requestCode == CODE_BACKGROUND_IMAGE) {
                backgroundImageView.setImageBitmap(imageBitmap)
            } else {
                profilePhotoView.setImageBitmap(imageBitmap)
            }

            //CONVERTENDO A IMAGEM NO FORMATO BITMAP PARA FILE
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg")
            file.createNewFile()

            val bos = ByteArrayOutputStream()
            imageBitmap?.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            //SALVANDO FILE NA VARIÁVEL PARA SER USADA NO UPDATE
            if (requestCode == CODE_BACKGROUND_IMAGE) {
                changedBackgroundImage = file
            } else {
                changedProfilePhoto = file
            }
        }
    }

    private fun openGallery(CODE_RESULT: Int) {

        //Abrir a galeria de imagens do dispositivo
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        //Abrir a Activity responsável por exibir as imagens
        //esta activity retornará o conteúdo selecionado
        //para o nosso app
        startActivityForResult(
            Intent.createChooser(intent,
                "Escolha uma foto"),
            CODE_RESULT
        )
    }
}
