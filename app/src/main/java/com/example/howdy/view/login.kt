package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.CadastroActivity
import com.example.howdy.R
import com.example.howdy.databinding.ActivityLoginBinding
import com.example.howdy.http.HttpHelper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        val esqueciSenha = findViewById<TextView>(R.id.esqueci_senha)
        val registrarConta = findViewById<TextView>(R.id.text_registar)

        auth = FirebaseAuth.getInstance()
        binding.buttonEntrar.setOnClickListener { login() }

        esqueciSenha.setOnClickListener {
            val RecuperarSenha =
                Intent(this, RecuperarSenha::class.java)
            startActivity(RecuperarSenha)
        }

        registrarConta.setOnClickListener {
            val ActivityTesteBinding =
                Intent(this, CadastroActivity::class.java)
            startActivity(ActivityTesteBinding)
        }

    }

    private fun login() {
        val email = binding.textEmail.text.toString()
        val senha = binding.textSenha.text.toString()

        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful){
                val idToken: Task<GetTokenResult>? = auth.currentUser?.getIdToken(true)
                val uid:String? = auth.currentUser?.uid

                //Tendo a certeza de que um idToken chegou
                if (idToken != null) {
                    //O USUÁRIO SE LOGOU NO FIREBASE, E AGORA IRÁ VER SE REALMENTE ESTÁ CADASTRADO NO BANCO SQL
                        doAsync {
                            val http = HttpHelper()
                            //val result = http.get("/users/isMyUidExternalRegistered", idToken)

                            uiThread {
                                println("DEBUGANDOTOKEN" + idToken)
                                //println("DEPOIS" + result)

                                Toast.makeText(applicationContext,"Login executado com sucesso! e ${idToken}", Toast.LENGTH_LONG).show()
                                //postagem()
                            }
                        }
                }
            }else{
                Toast.makeText(applicationContext,"Houve um erro no login", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun postagem() {
        val postar = Intent(this, paginaDePostagem::class.java)
        startActivity(postar)
    }


}