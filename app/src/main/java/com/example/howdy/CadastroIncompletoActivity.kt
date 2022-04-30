package com.example.howdy

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.howdy.databinding.ActivityCadastroIncompletoBinding
import com.example.howdy.model.*
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.view.paginaDePostagem
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import convertBrStringToDate
import convertDateToBackendFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CadastroIncompletoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroIncompletoBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var routerInterface: RouterInterface

    private lateinit var birthDateFormatted: String
    var targetLanguage  = TargetLanguage(0, "", "")
    var nativeLanguage = NativeLanguage(0, "", "")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroIncompletoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Criação do calendario
        val calendario = Calendar.getInstance()

        //Determinar o dia mes e ano do calendario
        val ano =calendario.get(Calendar.YEAR)
        val mes =calendario.get(Calendar.MONTH)
        val dia =calendario.get(Calendar.DAY_OF_MONTH)

        //Pegando o id para abrir o componente DatePincker
        val etDataNascimento = findViewById<TextInputEditText>(R.id.textData)

        etDataNascimento.setOnClickListener {
            val data = DatePickerDialog(
                this, { view, _ano, _mes, _dia ->
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

        with(binding.selectedIdiomaInteresse){
            setAdapter(adapter)
        }

        with(binding.selectedIdiomaNativo){
            setAdapter(adapter)
        }

        auth = FirebaseAuth.getInstance()
        binding.buttonCadastrar.setOnClickListener { cadastrar() }
    }

    private fun cadastrar() {
        val userName = binding.textNome.text.toString()
        val birthDate = binding.textData.text.toString()
        val nativeLanguageName = binding.selectedIdiomaNativo.text.toString()
        val targetLanguageName = binding.selectedIdiomaInteresse.text.toString()

        val validationResult = isFormValid(userName, birthDate, nativeLanguageName, targetLanguageName)
        if(validationResult != "") return Toast.makeText(applicationContext,validationResult, Toast.LENGTH_LONG).show()

        targetLanguage  = TargetLanguage(0, "", "")
        nativeLanguage = NativeLanguage(0, "", "")

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
        val birthDateDate = convertBrStringToDate(birthDate)
        birthDateFormatted = convertDateToBackendFormat(birthDateDate)

        //RESGATANDO IDTOKEN DO USUÁRIO LOGADO NO FIREBASE
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token
                if (idToken != null) {
                    //CADASTRANDO O USUÁRIO NO BANCO SQL
                    //CADASTRANDO O USUÁRIO NO BANCO SQL
                    val user = UserCreation(
                        userName,
                        birthDateFormatted,
                        targetLanguage,
                        nativeLanguage
                    )

                    routerInterface = APIUtil.getInterface()
                    createUser(user, idToken)
                }
            }
    }

    private fun createUser(usuario: UserCreation, idToken: String) {
        val call: Call<MySqlResult> = routerInterface.createUser(usuario, idToken)
        /** EXECUÇÃO CHAMADA DA ROTA  */
        call.enqueue(object : Callback<MySqlResult> {
            override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                if(response.isSuccessful) {
                    val userLogged = User()
                    userLogged.idUser = response.body()!!.insertId
                    userLogged.birthDate = birthDateFormatted
                    userLogged.backgroundImage = ""
                    userLogged.howdyCoin = 0
                    userLogged.idNativeLanguage = nativeLanguage.idNativeLanguage
                    userLogged.nativeLanguageName = nativeLanguage.nativeLanguageName
                    userLogged.nativeLanguageTranslatorName = nativeLanguage.nativeLanguageTranslatorName
                    userLogged.idTargetLanguage = targetLanguage.idTargetLanguage
                    userLogged.targetLanguageName = targetLanguage.targetLanguageName
                    userLogged.targetLanguageTranslatorName = targetLanguage.targetLanguageTranslatorName
                    userLogged.profilePhoto = ""
                    userLogged.subscriptionEndDate = ""

                    //SALVANDO ALGUNS DADOS DO USUÁRIO LOGADO NO SHARED PREFS
                    val userLoggedFile = getSharedPreferences(
                        "userLogged", Context.MODE_PRIVATE)

                    // EDIÇÃO DE DADOS DO ARQUIVO SHARED PREFERENCES
                    val editor = userLoggedFile.edit()
                    editor.putInt("idUser", userLogged.idUser)
                    editor.putString("birthDate", birthDateFormatted)
                    editor.putString("backgroundImage", userLogged.backgroundImage)
                    editor.putInt("howdyCoin", userLogged.howdyCoin)
                    editor.putInt("idNativeLanguage", userLogged.idNativeLanguage)
                    editor.putString("nativeLanguageName", userLogged.nativeLanguageName)
                    editor.putString("nativeLanguageTranslatorName", userLogged.nativeLanguageTranslatorName)
                    editor.putInt("idTargetLanguage", userLogged.idTargetLanguage)
                    editor.putString("targetLanguageName", userLogged.targetLanguageName)
                    editor.putString("targetLanguageTranslatorName", userLogged.targetLanguageTranslatorName)
                    editor.putString("profilePhoto", userLogged.profilePhoto)
                    editor.putString("subscriptionEndDate", userLogged.subscriptionEndDate)
                    editor.apply()

                    Toast.makeText(applicationContext,"Cadastro realizado com sucesso!",
                        Toast.LENGTH_LONG).show()

                    navigateToPostPage()
                } else {
                    Toast.makeText(applicationContext,"Ops... Houve um erro no cadastro",
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

    private fun isFormValid(userName:String, birthDate:String, nativeLanguageName:String, targetLanguageName:String):String{
        if (nativeLanguageName == targetLanguageName) return "O idioma nativo, e de interesse devem ser diferentes"
        return ""
    }

    private fun navigateToPostPage() {
        val targetPage = Intent(this, paginaDePostagem::class.java)
        startActivity(targetPage)
    }
}