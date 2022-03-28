package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.howdy.R
import com.example.howdy.databinding.ActivityCadastroBinding
import com.example.howdy.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.example.howdy.view.login as login

class Cadastro : AppCompatActivity() {

    private lateinit var textIdiomaNativo: AutoCompleteTextView
    private lateinit var inputLayoutIdiomaNativo: TextInputLayout

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonCadastrar.setOnClickListener{ cadastro() }

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

    private fun cadastro() {
        val email = binding.textEmail.text.toString()
        val senha = binding.textSenha.text.toString()

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener{
            if (it.isSuccessful){
                val usuario = auth.currentUser
                usuario!!.sendEmailVerification().addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(applicationContext,"Foi enviado o email para verificação",Toast.LENGTH_LONG).show()
                        login()

                    }else{
                        Toast.makeText(applicationContext,"Foi enviado o email para verificação",Toast.LENGTH_LONG).show()
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
