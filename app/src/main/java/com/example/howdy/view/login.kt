package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.CadastroActivity
import com.example.howdy.CadastroIncompletoActivity
import com.example.howdy.R
import com.example.howdy.databinding.ActivityLoginBinding
import com.example.howdy.databinding.ActivityPaginaDePostagemBinding
import com.example.howdy.databinding.FragmentHomeBinding
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
        binding.buttonEntrar.setOnClickListener { cadastrar() }

        esqueciSenha.setOnClickListener {
            val RecuperarSenha =
                Intent(this, RecuperarSenha::class.java)
            startActivity(RecuperarSenha)
        }

        registarConta.setOnClickListener {
            val ActivityTesteBinding =
                Intent(this, CadastroIncompletoActivity::class.java)
            startActivity(ActivityTesteBinding)
        }

    }

    private fun cadastrar() {
        val email = binding.textEmail.text.toString()
        val senha = binding.textSenha.text.toString()

        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful){
                val token = auth.currentUser?.uid
                Toast.makeText(applicationContext,"Login executado com sucesso! e ${token}" ,
                Toast.LENGTH_LONG).show()
                postagem()
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