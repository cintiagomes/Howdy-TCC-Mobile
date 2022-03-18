package com.example.howdy.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.howdy.R

class Cadastro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_cadastro)
    }
}