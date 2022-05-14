package com.example.howdy

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import com.example.howdy.databinding.ActivityConfiguracaoBinding
import com.example.howdy.model.UserTypes.User
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.convertStringtoEditable
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import convertDateToBrString
import kotlinx.android.synthetic.main.activity_configuracao.*
import kotlinx.android.synthetic.main.activity_configuracao.arrow_button
import kotlinx.android.synthetic.main.dialog_view.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val CODE_IMAGE = 100
const val CODE_PROFILE_PHOTO = 110

class ConfiguracaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracaoBinding
    private val context = this
    var imageBitmap : Bitmap? = null

    private lateinit var routerInterface: RouterInterface
    private val auth = FirebaseAuth.getInstance()

    private lateinit var userLogged: User

    private lateinit var nativeLanguageNameView: AutoCompleteTextView
    private lateinit var targetLanguageNameView: AutoCompleteTextView
    private lateinit var ibChangeUserProfilePhoto: ImageButton
    private lateinit var ibChangeBakcgroundCoverPhotoView: ImageButton
    private lateinit var btnDelteAccountView: Button
    private lateinit var userNameView: TextInputEditText
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
        birthDateView = textData
        emailView = textEmail
        passwordView = textSenha
        passwordConfirmationView = textSenhaConfirmar
        buttonToEdit = buttonAlterar
        btnDelteAccountView = btnDeleteAccount
        ibChangeBakcgroundCoverPhotoView = tvt_trocar_foto
        ibChangeUserProfilePhoto = tvt_trocar_foto_user

        routerInterface = APIUtil.`interface`
        findAndRenderUserData()
        setOptionsToChangePhoto()

        //BOTÃO CORRESPONDENTE A SETA DE VOLTAR
        arrowButton.setOnClickListener{ finish() }

        btnDelteAccountView.setOnClickListener{ deleteUser()}
    }

    private fun deleteUser(){
        val view = View.inflate(this@ConfiguracaoActivity, R.layout.dialog_view, null)
        val builder = AlertDialog.Builder(this@ConfiguracaoActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        view.bnt_cancelar.setOnClickListener {
            dialog.dismiss()
        }

        view.bnt_excluir.setOnClickListener {
            routerInterface = APIUtil.`interface`
//                val call: Call<MySqlResult> = routerInterface.deleteMyAccountOnBackend()


            dialog.dismiss()
        }
    }

    private fun setOptionsToChangePhoto(){
        /** TRATANDO IMAGENS **/
        //Caso a imagem seja um vector asset
        imageBitmap =
            resources
                .getDrawable(R.drawable.ic_pessoa)
                .toBitmap()

        ibChangeBakcgroundCoverPhotoView.setOnClickListener {
            carregarGaleria()
        }

        ibChangeUserProfilePhoto.setOnClickListener {
            openGallery()
        }
    }

    private fun findAndRenderUserData(){
        //COLETANDO ID DO USUÁRIO LOGADO
        val userLoggedFile = this.getSharedPreferences(
            "userLogged", Context.MODE_PRIVATE)

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
                            println("DEBUGANDO - ONFAILURE EM GET USER BY ID $t")
                        }
                    })
                }
            }
    }

    fun renderUserData(){
        userNameView.text = convertStringtoEditable(userLogged.userName)
        println("DEBUGANDO data" + userLogged.birthDate)
        birthDateView.text = convertStringtoEditable(convertDateToBrString(userLogged.birthDate))
        emailView.text = convertStringtoEditable("xxxxx@gmail.com")

        birthDateView.setOnClickListener {
            val data = DatePickerDialog(
                this, { _, _year, _month, _day ->
                    birthDateView.setText("$_day/${_month + 1}/$_year")
                }, userLogged.birthDate.year, userLogged.birthDate.month, userLogged.birthDate.day)
            data.show()
        }

        /** LISTANDO IDIOMA DE INTERESSE, NATIVO, E SELECIONANDO A QUE JÁ É CORRESPONDENTE A DO USUÁRIO **/
        val countries = resources.getStringArray(R.array.linguagens)
        val countriesWithoutNativeLanguageName = countries.filter { s -> s != userLogged.nativeLanguageName }
        val countriesWithoutTargetLanguageName = countries.filter { s -> s != userLogged.targetLanguageName }

        val adapterWithoutNativeLanguageName = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            countriesWithoutNativeLanguageName
        )

        val adapterWithoutTargetLanguageName = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            countriesWithoutTargetLanguageName
        )

        with(nativeLanguageNameView){
            setAdapter(adapterWithoutNativeLanguageName)
        }
        nativeLanguageNameView.text = convertStringtoEditable(userLogged.nativeLanguageName)

        with(targetLanguageNameView){
            setAdapter(adapterWithoutTargetLanguageName)
        }
        targetLanguageNameView.text = convertStringtoEditable(userLogged.targetLanguageName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imagem: Intent?) {
        super.onActivityResult(requestCode, resultCode, imagem)

        if (requestCode == CODE_IMAGE && resultCode == -1){
            //Recuperar a imagem do stream(fluxo)
            val fluxoImagem = contentResolver.openInputStream(imagem!!.data!!)

            //Converter os bits em um bitmap
            imageBitmap = BitmapFactory.decodeStream(fluxoImagem)

            //Colocar o bitmap no ImageView
            userImage.setImageBitmap(imageBitmap)

        }
        if (requestCode == CODE_PROFILE_PHOTO && resultCode == -1){

            //Recuperar a imagem do stream(fluxo)
            val fluxoImagem = contentResolver.openInputStream(imagem!!.data!!)

            //Converter os bits em um bitmap
            imageBitmap = BitmapFactory.decodeStream(fluxoImagem)

            userBackground.setImageBitmap(imageBitmap)

        }

    }

    private fun openGallery() {

        //Abrir a galeria de imagens do dispositivo
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        //Abrir a Activity responsável por exibir as imagens
        //esta activity retornará o conteúdo selecionado
        //para o nosso app
        startActivityForResult(
            Intent.createChooser(intent,
                "Escolha uma foto"),
            CODE_IMAGE
        )

    }

    private fun carregarGaleria() {

        //Abrir a galeria de imagens do dispositivo
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        //Abrir a Activity responsável por exibir as imagens
        //esta activity retornará o conteúdo selecionado
        //para o nosso app
        startActivityForResult(
            Intent.createChooser(intent,
                "Escolha uma foto"),
            CODE_PROFILE_PHOTO
        )

    }
}
