package com.example.howdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.howdy.view.paginaDePostagem

class ConfiguracoesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_configuracoes)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button)

        arrowButton.setOnClickListener{
            val arrowButton =
                Intent(this, paginaDePostagem::class.java)
            startActivity(arrowButton)
        }
    }
}