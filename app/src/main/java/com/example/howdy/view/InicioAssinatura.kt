package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import com.example.howdy.R

class InicioAssinatura : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_inicio_assinatura)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button)

        arrowButton.setOnClickListener{
            val arrowButton =
                Intent(this, paginaDePostagem::class.java)
            startActivity(arrowButton)
        }

    }

}