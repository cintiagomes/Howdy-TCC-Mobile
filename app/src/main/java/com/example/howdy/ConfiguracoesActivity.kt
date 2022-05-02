package com.example.howdy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ConfiguracoesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_configuracoes)
    }
}