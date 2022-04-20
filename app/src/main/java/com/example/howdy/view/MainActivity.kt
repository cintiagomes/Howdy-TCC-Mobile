package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.howdy.CadastroActivity
import com.example.howdy.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //CHECANDO SE O USUÁRIO JÁ ESTÁ LOGADO NO FIREBASE, E FORNECENDO UM REDIRECIONAMENTO
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null){
            val postar = Intent(this, paginaDePostagem::class.java)
            startActivity(postar)
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
                login::class.java)
            startActivity(intent)

        }

        registrarConta.setOnClickListener {
            val TesteActivity =
                Intent(this, CadastroActivity::class.java)
            startActivity(TesteActivity)
        }

    }
}