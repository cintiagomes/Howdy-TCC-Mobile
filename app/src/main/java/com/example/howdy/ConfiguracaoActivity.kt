//package com.example.howdy
//
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Editable
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AlertDialog
//import androidx.core.graphics.drawable.toBitmap
//import com.example.howdy.adapter.PerfilFragmentTypeAdapter
//import com.example.howdy.databinding.ActivityCadastroBinding
//import com.example.howdy.databinding.ActivityConfiguracaoBinding
//import com.example.howdy.model.UserTypes.UserCollectedWithId
//import com.example.howdy.remote.APIUtil
//import com.example.howdy.remote.RouterInterface
//import com.google.android.material.tabs.TabLayoutMediator
//import com.google.android.material.textfield.TextInputEditText
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.android.synthetic.main.activity_configuracao.*
//import kotlinx.android.synthetic.main.activity_configuracao.arrow_button
//import kotlinx.android.synthetic.main.activity_perfil.*
//import kotlinx.android.synthetic.main.dialog_view.view.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.http.Header
//
//const val CODE_IMAGEM = 100
//const val CODE_FOTO = 110
//
//class ConfiguracaoActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityConfiguracaoBinding
//    var imageBitmap : Bitmap? = null
//
//    private lateinit var routerInterface: RouterInterface
//    private val auth = FirebaseAuth.getInstance()
//
//    private lateinit var nativeLanguageNameView: AutoCompleteTextView
//    private lateinit var targetLanguageNameView: AutoCompleteTextView
//    private lateinit var userNameView: TextInputEditText
//    private lateinit var birthDateView: TextInputEditText
//    private lateinit var emailView: TextInputEditText
//    private lateinit var passwordView: TextInputEditText
//    private lateinit var passwordConfirmationView: TextInputEditText
//    private lateinit var buttonToEdit: Button
//    private lateinit var userLogged: UserCollectedWithId
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val actionBar = supportActionBar
//        actionBar!!.hide()
//        binding = ActivityConfiguracaoBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val arrowButton = arrow_button
//
//        nativeLanguageNameView = binding.selectedIdiomaNativo
//        targetLanguageNameView = binding.selectedIdiomaInteresse
//        val userNameView = inputLayoutEmail
//        val birthDateView = textData
//        val emailView = textEmail
//        val passwordView = textSenha
//        val passwordConfirmationView = textSenhaConfirmar
//        val buttonToEdit = buttonAlterar
//
//        val buttonExcluir = buttonExcluir
//        val tvtTrocarFoto = tvt_trocar_foto
//        val tvtTrocarFotoUser = tvt_trocar_foto_user
//
//        //COLETANDO ID DO USUÁRIO LOGADO
//        val userLoggedFile = this.getSharedPreferences(
//            "userLogged", Context.MODE_PRIVATE)
//
//        val idUser = userLoggedFile.getInt("idUser", 0)
//
//        //COLETANDO INFORMAÇÕES ATUAIS DO USUÁRIO LOGADO, PARA ENTÃO PREENCHER NO INPUT
//
//        auth.currentUser?.getIdToken(true)
//            ?.addOnSuccessListener { result ->
//                val idToken = result.token
//                if (idToken != null) {
//                    val call: Call<List<UserCollectedWithId>> = routerInterface.getUserById(idToken, idUser)
//                    call.enqueue(object : Callback<List<UserCollectedWithId>> {
//                        override fun onResponse(call: Call<List<UserCollectedWithId>>, response: Response<List<UserCollectedWithId>>) {
//                            if (response.isSuccessful) {
//                                userLogged = response.body()?.get(0)!!
////                                renderUserData()
//
////                                val adapter = PerfilFragmentTypeAdapter(context, user)
////                                viewpager2.adapter = adapter
////
////                                TabLayoutMediator(tabLayout, viewpager2){ tab, pos ->
////                                    when(pos){
////                                        0 -> {tab.text="Postagens"}
////                                        1 -> {tab.text="Amigos"}
////                                        2 -> {tab.text="Aprendizado"}
////                                        3 -> {tab.text="Ensinamentos"}
////                                    }
////                                }.attach()
////                            } else {
////                                Toast.makeText(
////                                    context, "OPS... OCORREU UM ERRO AO RESGATAR OS DADOS DO USUÁRIO!",
////                                    Toast.LENGTH_LONG
////                                ).show()
////                            }
//                        }
//
//                        override fun onFailure(call: Call<List<UserCollectedWithId>>, t: Throwable) {
//                            Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
//                                Toast.LENGTH_LONG).show()
//                            println("DEBUGANDO - ONFAILURE EM GET USER BY ID $t")
//                        }
//                    })
//                }
//            }
//    }
//
//
//        /** LISTANDO IDIOMA DE INTERESSE, E NATIVO **/
//        //RESGATANDO ATUAL LÍNGUA DE INTERESSE E NATIVA DO USUÁRIO
//        val userLoggedFile = this.getSharedPreferences(
//            "userLogged", Context.MODE_PRIVATE)
//
//        val nativeLanguageName = userLoggedFile.getString("nativeLanguageName", "")
//        val targetLanguageName = userLoggedFile.getString("targetLanguageName", "")
//
//        val countries = resources.getStringArray(R.array.linguagens)
//        val countriesWithoutNativeLanguageName = countries.filter { s -> s != nativeLanguageName }
//        val countriesWithoutTargetLanguageName = countries.filter { s -> s != targetLanguageName }
//
//        val adapterWithoutNativeLanguageName = ArrayAdapter(
//            this,
//            R.layout.dropdown_item,
//            countriesWithoutNativeLanguageName
//        )
//
//        val adapterWithoutTargetLanguageName = ArrayAdapter(
//            this,
//            R.layout.dropdown_item,
//            countriesWithoutTargetLanguageName
//        )
//
//        with(nativeLanguageNameView){
//            setAdapter(adapterWithoutNativeLanguageName)
//        }
//        nativeLanguageNameView.text = Editable.Factory.getInstance().newEditable(nativeLanguageName)
//
//        with(targetLanguageNameView){
//            setAdapter(adapterWithoutTargetLanguageName)
//        }
//        targetLanguageNameView.text = Editable.Factory.getInstance().newEditable(targetLanguageName)
//
//        /** TRATANDO IMAGENS **/
//
//        //Caso a imagem seja um vector asset
//        imageBitmap =
//            resources
//                .getDrawable(R.drawable.ic_pessoa)
//                .toBitmap()
//
//        tvtTrocarFoto.setOnClickListener {
//            carregarGaleria()
//        }
//
//        tvtTrocarFotoUser.setOnClickListener {
//            openGallery()
//        }
//
//        arrowButton.setOnClickListener{ finish() }
//
//        buttonExcluir.setOnClickListener{
//
//            val view = View.inflate(this@ConfiguracaoActivity, R.layout.dialog_view, null)
//            val builder = AlertDialog.Builder(this@ConfiguracaoActivity)
//            builder.setView(view)
//
//            val dialog = builder.create()
//            dialog.show()
//            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//            dialog.setCancelable(false)
//
//            view.bnt_cancelar.setOnClickListener {
//                dialog.dismiss()
//            }
//
//            view.bnt_excluir.setOnClickListener {
//                routerInterface = APIUtil.`interface`
////                val call: Call<MySqlResult> = routerInterface.deleteMyAccountOnBackend()
//
//
//                dialog.dismiss()
//            }
//
//        }
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, imagem: Intent?) {
//        super.onActivityResult(requestCode, resultCode, imagem)
//
//        if (requestCode == CODE_IMAGEM && resultCode == -1){
//            //Recuperar a imagem do stream(fluxo)
//            val fluxoImagem = contentResolver.openInputStream(imagem!!.data!!)
//
//            //Converter os bits em um bitmap
//            imageBitmap = BitmapFactory.decodeStream(fluxoImagem)
//
//            //Colocar o bitmap no ImageView
//            userImage.setImageBitmap(imageBitmap)
//
//        }
//        if (requestCode == CODE_FOTO && resultCode == -1){
//
//            //Recuperar a imagem do stream(fluxo)
//            val fluxoImagem = contentResolver.openInputStream(imagem!!.data!!)
//
//            //Converter os bits em um bitmap
//            imageBitmap = BitmapFactory.decodeStream(fluxoImagem)
//
//            userBackground.setImageBitmap(imageBitmap)
//
//        }
//
//    }
//
//    private fun openGallery() {
//
//        //Abrir a galeria de imagens do dispositivo
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//
//        //Abrir a Activity responsável por exibir as imagens
//        //esta activity retornará o conteúdo selecionado
//        //para o nosso app
//        startActivityForResult(
//            Intent.createChooser(intent,
//                "Escolha uma foto"),
//            CODE_IMAGEM
//        )
//
//    }
//
//    private fun carregarGaleria() {
//
//        //Abrir a galeria de imagens do dispositivo
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//
//        //Abrir a Activity responsável por exibir as imagens
//        //esta activity retornará o conteúdo selecionado
//        //para o nosso app
//        startActivityForResult(
//            Intent.createChooser(intent,
//                "Escolha uma foto"),
//            CODE_FOTO
//        )
//
//    }
//}
