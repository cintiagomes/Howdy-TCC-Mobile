package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.howdy.R

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_login)

        val esqueciSenha = findViewById<TextView>(R.id.esqueci_senha)
        val registarConta = findViewById<TextView>(R.id.text_registar)
        val buttonEntrar = findViewById<Button>(R.id.button_entrar)

        esqueciSenha.setOnClickListener {
            val RecuperarSenha =
                Intent(this, RecuperarSenha::class.java)
            startActivity(RecuperarSenha)
        }

        buttonEntrar. setOnClickListener {

            val intent = Intent(
                this,
                paginaDePostagem::class.java)
            startActivity(intent)

        }

        registarConta.setOnClickListener {
            val Cadastro =
                Intent(this, Cadastro::class.java)
            startActivity(Cadastro)
        }

    }
}