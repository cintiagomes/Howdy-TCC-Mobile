package com.example.howdy.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.beust.klaxon.Klaxon
import com.example.howdy.CadastroActivity
import com.example.howdy.CadastroIncompletoActivity
import com.example.howdy.R
import com.example.howdy.http.HttpHelper
import com.example.howdy.model.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //CHECANDO SE O USUÁRIO JÁ ESTÁ LOGADO NO FIREBASE, E FORNECENDO UM REDIRECIONAMENTO
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null){
            val postPage = Intent(this, paginaDePostagem::class.java)

            //RESGATANDO IDTOKEN DO USUÁRIO LOGADO NO FIREBASE
            currentUser?.getIdToken(true)
                ?.addOnSuccessListener(OnSuccessListener<GetTokenResult> { result ->
                    val idToken = result.token

                    if (idToken != null) {
                        //O USUÁRIO SE LOGOU NO FIREBASE, E AGORA IRÁ VER SE REALMENTE ESTÁ CADASTRADO NO BANCO SQL
                        doAsync {
                            val http = HttpHelper()
                            val res = http.get("/users/isMyUidExternalRegistered", idToken)

                            uiThread {
                                val gson = Gson()

                                if(res != "This user does not have an account in our system") {
                                    //AGORA QUE SABEMOS QUE O USUÁRIO DE FATO ESTÁ LOGADO, O MANDAREMOS PARA A PRÓXIMA TELA

                                    //REDIRECIONANDO O USUÁRIO PARA A PÁGINA DE POSTAGENS
                                    startActivity(postPage)
                                }
                            }
                        }
                    }
                })
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
}