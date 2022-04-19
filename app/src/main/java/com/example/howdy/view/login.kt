package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.CadastroActivity
import com.example.howdy.R
import com.example.howdy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

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
        val registarConta = findViewById<TextView>(R.id.text_registar)

        auth = FirebaseAuth.getInstance()
        binding.buttonEntrar.setOnClickListener { autenticate() }

        esqueciSenha.setOnClickListener {
            val RecuperarSenha =
                Intent(this, RecuperarSenha::class.java)
            startActivity(RecuperarSenha)
        }

        registarConta.setOnClickListener {
            val ActivityTesteBinding =
                Intent(this, CadastroActivity::class.java)
            startActivity(ActivityTesteBinding)
        }

    }

    private fun autenticate() {
        val email = binding.textEmail.text.toString()
        val password = binding.textSenha.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                val token = auth.currentUser?.uid

                Toast.makeText(applicationContext,"Login executado com sucesso! e ${token}",
                Toast.LENGTH_LONG).show()

                //RECUPERANDO DADOS DO USUÁRIO PRESENTES NO BANCO MYSQL

                goToPostsActivity()
            }else{
                Toast.makeText(applicationContext,"Houve um erro no login", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToPostsActivity() {
        val postar = Intent(this, paginaDePostagem::class.java)
        startActivity(postar)
    }


}