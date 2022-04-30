package com.example.howdy.view

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
        routerInterface = APIUtil.getInterface()

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
                    //AGORA QUE SABEMOS QUE O USUÁRIO DE FATO ESTÁ LOGADO, O MANDAREMOS PARA A PRÓXIMA TELA
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