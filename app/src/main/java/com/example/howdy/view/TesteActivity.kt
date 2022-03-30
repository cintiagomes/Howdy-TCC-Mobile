package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.howdy.R
import com.example.howdy.databinding.ActivityLoginBinding
import com.example.howdy.databinding.ActivityTesteBinding
import com.google.firebase.auth.FirebaseAuth

class TesteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTesteBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        binding = ActivityTesteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.buttonCadastrar.setOnClickListener { cadastrar() }

        val countries = resources.getStringArray(R.array.linguagens)
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            countries
        )

        with(binding.textIdiomaNativo){
            setAdapter(adapter)
        }

        with(binding.textIdiomaInterrece){
            setAdapter(adapter)
        }

    }

    private fun cadastrar() {
        val email = binding.textEmail.text.toString()
        val senha = binding.textSenha.text.toString()

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful){
                val usuario = auth.currentUser
                usuario!!.sendEmailVerification().addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(applicationContext,"Foi enviado o email para verificação",Toast.LENGTH_LONG).show()
                        login()
                    }else{
                        Toast.makeText(applicationContext,"Houve um erro no cadastro",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    private fun login(){
        val intent = Intent(this, ActivityLoginBinding::class.java)
        startActivity(intent)
    }

}