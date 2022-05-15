package com.example.howdy.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.howdy.CadastroActivity
import com.example.howdy.R
import com.example.howdy.model.UserTypes.User
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var routerInterface: RouterInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        //CHECANDO SE O USUÁRIO JÁ ESTÁ LOGADO NO FIREBASE, E FORNECENDO UM REDIRECIONAMENTO
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        /** CONFIGURAÇÃO DO routerInterface **/
        routerInterface = APIUtil.`interface`

        val currentUser = auth.currentUser
        if(currentUser != null){
            //RESGATANDO IDTOKEN DO USUÁRIO LOGADO NO FIREBASE
            currentUser.getIdToken(true)
                .addOnSuccessListener { result ->
                    val idToken = result.token

                    if (idToken != null) {
                        //O USUÁRIO SE LOGOU NO FIREBASE, E AGORA IRÁ VER SE REALMENTE ESTÁ CADASTRADO NO BANCO SQL
                        isMyUidExternalRegistered(idToken)
                    }
                }
        }


        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_main)

        val registrarConta = findViewById<TextView>(R.id.link_registar)
        val buttonEntrar = findViewById<Button>(R.id.button_entrar)

        buttonEntrar. setOnClickListener {
            val intent = Intent(
                this,
                Login::class.java)
            startActivity(intent)

        }

        registrarConta.setOnClickListener {
            val TesteActivity =
                Intent(this, CadastroActivity::class.java)
            startActivity(TesteActivity)
        }

    }

    fun isMyUidExternalRegistered(idToken: String) {
        val call: Call<List<User>> = routerInterface.isMyUidExternalRegistered(idToken)
        /** EXECUÇÃO CHAMADA DA ROTA  */
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    //PEGANDO DADOS MAIS ATUALIZADOS DO USUÁRIO EM QUESTÃO
                    val userLogged = response.body()!![0]

                    val userLoggedFile = getSharedPreferences(
                        "userLogged", Context.MODE_PRIVATE)

                    // EDIÇÃO DE DADOS DO ARQUIVO SHARED PREFERENCES
                    val editor = userLoggedFile.edit()
                    editor.putInt("idUser", userLogged.idUser)
                    editor.putString("profilePhoto", userLogged.profilePhoto)
                    editor.putString("userName", userLogged.userName)
                    editor.putString("description", userLogged.description)
                    editor.putString("backgroundImage", userLogged.backgroundImage)
                    editor.putString("subscriptionEndDate", userLogged.subscriptionEndDate.toString())
                    editor.putInt("howdyCoin", userLogged.howdyCoin)
                    editor.putInt("idTargetLanguage", userLogged.idTargetLanguage)
                    editor.putString("targetLanguageName", userLogged.targetLanguageName)
                    editor.putString("targetLanguageTranslatorName", userLogged.targetLanguageTranslatorName)
                    editor.putInt("idNativeLanguage", userLogged.idNativeLanguage)
                    editor.putString("nativeLanguageName", userLogged.nativeLanguageName)
                    editor.putString("nativeLanguageTranslatorName", userLogged.nativeLanguageTranslatorName)
                    editor.apply()

                    //AGORA QUE SABEMOS QUE O USUÁRIO DE FATO ESTÁ LOGADO, MANDAREMOS ELE PARA A PRÓXIMA TELA
                    val postPage = Intent(applicationContext, paginaDePostagem::class.java)
                    startActivity(postPage)
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                println("ERRO-API: " + t.message)
            }
        })
    }
}