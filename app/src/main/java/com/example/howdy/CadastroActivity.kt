package com.example.howdy

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.howdy.databinding.ActivityCadastroBinding
import com.example.howdy.model.*
import com.example.howdy.model.UserTypes.NativeLanguage
import com.example.howdy.model.UserTypes.TargetLanguage
import com.example.howdy.model.UserTypes.User
import com.example.howdy.model.UserTypes.DataToCreateUser
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.view.paginaDePostagem
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.example.howdy.utils.convertBrStringToDate
import com.example.howdy.utils.convertDateToBackendFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var routerInterface: RouterInterface

    private lateinit var birthDateStringBackend: String
    private lateinit var birthDateDate: Date
    var targetLanguage  = TargetLanguage(0, "", "")
    var nativeLanguage = NativeLanguage(0, "", "")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        //Criação do calendario
        val calendario = Calendar.getInstance()

        //Determinar o dia mes e ano do calendario
        val ano =calendario.get(Calendar.YEAR) - 18
        val mes =calendario.get(Calendar.MONTH)
        val dia =calendario.get(Calendar.DAY_OF_MONTH)

        //Pegando o id para abrir o componente DatePincker
        val etDataNascimento = findViewById<TextInputEditText>(R.id.textData)

        etDataNascimento.setOnClickListener {
            val data = DatePickerDialog(
                this, { _, _ano, _mes, _dia ->
                    etDataNascimento.setText("$_dia/${_mes + 1}/$_ano")
                }, ano, mes, dia)
            data.show()
        }

        val countries = resources.getStringArray(R.array.linguagens)
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            countries
        )

        with(binding.selectedIdiomaNativo){
            setAdapter(adapter)
        }

        with(binding.selectedIdiomaInteresse){
            setAdapter(adapter)
        }

        val btnRegistrar = findViewById<TextView>(R.id.link_registar)

        auth = FirebaseAuth.getInstance()
        binding.buttonCadastrar.setOnClickListener { cadastrar() }

        btnRegistrar.setOnClickListener {
            val login =
                Intent(this, com.example.howdy.view.Login::class.java)
            startActivity(login)
        }
    }

    private fun cadastrar() {
        val userName = binding.textNome.text.toString()
        val birthDate = binding.textData.text.toString()
        val email = binding.textEmail.text.toString()
        val nativeLanguageName = binding.selectedIdiomaNativo.text.toString()
        val targetLanguageName = binding.selectedIdiomaInteresse.text.toString()
        val password = binding.textSenha.text.toString()
        val passwordConfirmation = binding.textConfirmacaoDeSenha.text.toString()

        val validationResult = isFormValid(userName, birthDate, email, nativeLanguageName, targetLanguageName, password, passwordConfirmation)
        if(validationResult != "") return Toast.makeText(applicationContext,validationResult, Toast.LENGTH_LONG).show()

        //DEFININDO QUAIS SERÃO OS OBJETOS DE TARGET E NATIVE LANGUAGE
        if (nativeLanguageName == "Português brasileiro"){
            nativeLanguage.idNativeLanguage = 1
            nativeLanguage.nativeLanguageName = "Português brasileiro"
            nativeLanguage.nativeLanguageTranslatorName = "pt"

            targetLanguage.idTargetLanguage = 2
            targetLanguage.targetLanguageName = "Inglês americano"
            targetLanguage.targetLanguageTranslatorName = "en"
        } else {
            targetLanguage.idTargetLanguage = 1
            targetLanguage.targetLanguageName = "Português brasileiro"
            targetLanguage.targetLanguageTranslatorName = "pt"

            nativeLanguage.idNativeLanguage = 2
            nativeLanguage.nativeLanguageName = "Inglês americano"
            nativeLanguage.nativeLanguageTranslatorName = "en"
        }

        //CONVERTENDO DATA DE NASCIMENTO
        birthDateDate = convertBrStringToDate(birthDate)
        birthDateStringBackend = convertDateToBackendFormat(birthDateDate)

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful){
                //RESGATANDO IDTOKEN DO USUÁRIO LOGADO NO FIREBASE
                auth.currentUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val idToken = result.token
                        if (idToken != null) {
                            //CADASTRANDO O USUÁRIO NO BANCO SQL
                            val user = DataToCreateUser(
                                userName,
                                birthDateStringBackend,
                                targetLanguage,
                                nativeLanguage
                            )

                            routerInterface = APIUtil.`interface`
                            createUser(user, idToken)
                        }
                    }
            }else{
                Toast.makeText(applicationContext,"Ops! Houve um erro no cadastro",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createUser(user: DataToCreateUser, idToken: String) {
        val call: Call<MySqlResult> = routerInterface.createUser(user, idToken)
        /** EXECUÇÃO CHAMADA DA ROTA  */
        call.enqueue(object : Callback<MySqlResult> {
            override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                if(response.isSuccessful) {
                    val userLogged = User(
                        response.body()!!.insertId,
                        birthDateDate,
                        null,
                        user.userName,
                        null,
                        null,
                        null,
                        false,
                        0,
                        targetLanguage.idTargetLanguage,
                        targetLanguage.targetLanguageName,
                        targetLanguage.targetLanguageTranslatorName,
                        nativeLanguage.idNativeLanguage,
                        nativeLanguage.nativeLanguageName,
                        nativeLanguage.nativeLanguageTranslatorName,
                    )

                    //SALVANDO ALGUNS DADOS DO USUÁRIO LOGADO NO SHARED PREFS
                    val userLoggedFile = getSharedPreferences(
                        "userLogged", Context.MODE_PRIVATE)

                    // EDIÇÃO DE DADOS DO ARQUIVO SHARED PREFERENCES
                    val editor = userLoggedFile.edit()
                    editor.putInt("idUser", userLogged.idUser)
                    editor.putString("userName", userLogged.userName)
                    editor.putString("birthDate", userLogged.birthDate.toString())
                    editor.putString("backgroundImage", userLogged.backgroundImage)
                    editor.putInt("howdyCoin", userLogged.howdyCoin)
                    editor.putInt("idNativeLanguage", userLogged.idNativeLanguage)
                    editor.putString("nativeLanguageName", userLogged.nativeLanguageName)
                    editor.putString("nativeLanguageTranslatorName", userLogged.nativeLanguageTranslatorName)
                    editor.putInt("idTargetLanguage", userLogged.idTargetLanguage)
                    editor.putString("targetLanguageName", userLogged.targetLanguageName)
                    editor.putString("targetLanguageTranslatorName", userLogged.targetLanguageTranslatorName)
                    editor.putString("profilePhoto", userLogged.profilePhoto)
                    editor.putString("subscriptionEndDate",
                        userLogged.subscriptionEndDate?.toString()
                    )
                    editor.putString("description", userLogged.description)
                    editor.apply()

                    Toast.makeText(applicationContext,"Cadastro realizado com sucesso!",
                        Toast.LENGTH_LONG).show()

                    navigateToPostPage()
                } else {
                    Toast.makeText(applicationContext,"Ops! Houve um erro no cadastro",
                        Toast.LENGTH_LONG).show()

                    //DELETANDO USUÁRIO CRIADO, JÁ QUE ELE NÃO CONSEGUIU SER CADASTRADO NO BANCO
                    auth.currentUser?.delete()
                }
            }

            override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                println("ERRO-API" + t.message)
            }
        })
    }

    private fun isFormValid(userName:String, birthDate:String, email:String, nativeLanguageName:String, targetLanguageName:String, password:String, passwordConfirmation:String):String{
        if (password != passwordConfirmation) {
            val message = "As senhas precisam ser iguais"
            binding.textSenha.error = message
            return message
        }

        if (nativeLanguageName == targetLanguageName) {
            val message = "A língua nativa e a língua alvo não podem ser iguais!"
            binding.selectedIdiomaInteresse.error = message

            return message
        }

        if (userName.isEmpty()) {
            val message = "O campo nome não pode estar vazio!"
            binding.textNome.error = message

            return message
        }

        if (birthDate.isEmpty()) {
            val message = "O campo data de nascimento não pode estar vazio!"
            binding.textData.error = message

            return message
        }

        if (email.isEmpty()) {
            val message = "O campo email não pode estar vazio!"
            binding.textEmail.error = message

            return message
        }

        if (nativeLanguageName.isEmpty()) {
            val message = "O campo língua nativa não pode estar vazio!"
            binding.selectedIdiomaNativo.error = message

            return message
        }

        if (targetLanguageName.isEmpty()) {
            val message = "O campo língua de interesse não pode estar vazio!"
            binding.selectedIdiomaInteresse.error = message

            return message
        }

        if (password.isEmpty()) {
            val message = "O campo senha não pode estar vazio!"
            binding.textSenha.error = message

            return message
        }

        if (passwordConfirmation.isEmpty()) {
            val message = "O campo confirmação de senha não pode estar vazio!"
            binding.textConfirmacaoDeSenha.error = message

            return message
        }

        return ""
    }

    private fun navigateToPostPage() {
        val targetPage = Intent(this, paginaDePostagem::class.java)
        startActivity(targetPage)
    }
}