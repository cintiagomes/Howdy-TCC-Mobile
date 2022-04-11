package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.howdy.R

class CodigoDeRecuperacao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_codigo_de_recuperacao)

        val buttonVoltar = findViewById<Button>(R.id.button_cancelar)

        buttonVoltar.setOnClickListener {
            val login =
                Intent(this, login::class.java)
            startActivity(login)
        }
    }
}