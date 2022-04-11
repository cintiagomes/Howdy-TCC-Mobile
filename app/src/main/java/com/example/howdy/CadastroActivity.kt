
package com.example.howdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.howdy.databinding.ActivityCadastroBinding
import com.example.howdy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.buttonCadastrar.setOnClickListener { cadastrar() }

    }

    private fun cadastrar() {
        val email = binding.textEmail.text.toString()
        val senha = binding.textSenha.text.toString()

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(applicationContext,"Cadastro realizado com sucesso!",
                Toast.LENGTH_LONG).show()
                login()

            }else{
                Toast.makeText(applicationContext,"Houve um erro no seu cadastro",
                Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun login(){
        val intent = Intent(this, com.example.howdy.view.login::class.java)
        startActivity(intent)
    }

}