package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.howdy.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_main)

        val registarConta = findViewById<TextView>(R.id.text_registar)
        val buttonEntrar = findViewById<Button>(R.id.button_entrar)

        buttonEntrar. setOnClickListener {

            val intent = Intent(
                this,
                login::class.java)
            startActivity(intent)

        }

        registarConta.setOnClickListener {
            val Cadastro =
                Intent(this, Cadastro::class.java)
            startActivity(Cadastro)
        }

    }
}