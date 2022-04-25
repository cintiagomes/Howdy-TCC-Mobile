package com.example.howdy

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.beust.klaxon.Klaxon
import com.example.howdy.databinding.ActivityCadastroIncompletoBinding
import com.example.howdy.http.HttpHelper
import com.example.howdy.model.*
import com.example.howdy.view.Login
import com.example.howdy.view.paginaDePostagem
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.gson.Gson
import convertBrStringToDate
import convertDateToBackendFormat
import hadAnError
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class CadastroIncompletoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroIncompletoBinding
    private lateinit var auth: FirebaseAuth

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
                this, DatePickerDialog.OnDateSetListener{ view, _ano, _mes, _dia ->
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

        var targetLanguage  = TargetLanguage(0, "", "")
        var nativeLanguage = NativeLanguage(0, "", "")

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
        val birthDateFormatted = convertDateToBackendFormat(birthDateDate)

        //RESGATANDO IDTOKEN DO USUÁRIO LOGADO NO FIREBASE
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener(OnSuccessListener<GetTokenResult> { result ->
                val idToken = result.token
                if (idToken != null){
                    //CADASTRANDO O USUÁRIO NO BANCO SQL
                    val user = UserCreation(userName, birthDateFormatted, targetLanguage, nativeLanguage)

                    val gson = Gson()
                    val userInJson = gson.toJson(user)

                    doAsync {
                        val http = HttpHelper()
                        val resJson = http.post("/users", idToken, userInJson)

                        uiThread {
                            //CHECANDO SE NÃO HOUVE ERRO NA REQUISIÇÃO
                            val error = hadAnError(resJson)

                            if (error.status > 0){
                                println("DEBUGANDO: " + error.message)

                                Toast.makeText(applicationContext,"Ops... Houve um erro no cadastro",
                                    Toast.LENGTH_LONG).show()

                                //DELETANDO USUÁRIO CRIADO, JÁ QUE ELE NÃO CONSEGUIU SER CADASTRADO NO BANCO
                                auth.currentUser?.delete()
                            } else {
                                val res = Klaxon().parse<MySqlInsert>(resJson)

                                val userLogged = User()
                                userLogged.idUser = res!!.insertId
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
                                    "userLogged", MODE_PRIVATE
                                )

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
                            }
                        }
                    }
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