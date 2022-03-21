package com.example.howdy.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.howdy.R
import com.example.howdy.databinding.ActivityCadastroBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Cadastro : AppCompatActivity() {

    private lateinit var textIdiomaNativo: AutoCompleteTextView
    private lateinit var inputLayoutIdiomaNativo: TextInputLayout

    private lateinit var binding: ActivityCadastroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countries = resources.getStringArray(R.array.linguagens)
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            countries
        )

        with(binding.textIdiomaNativo){
            setAdapter(adapter)
        }


        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_cadastro)
    }
}