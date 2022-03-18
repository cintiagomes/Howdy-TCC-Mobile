package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.howdy.R

class RecuperarSenha : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_recuperar_senha)

        val buttonCancelar = findViewById<Button>(R.id.button_cancelar)
        val buttonProximo = findViewById<Button>(R.id.button_proximo)

        buttonCancelar.setOnClickListener {
            val login =
                Intent(this, login::class.java)
            startActivity(login)
        }

        buttonProximo.setOnClickListener {
            val codigoDeRecuperacao =
                Intent(this, CodigoDeRecuperacao::class.java)
            startActivity(codigoDeRecuperacao)
        }

    }
}