package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.howdy.R
import com.example.howdy.databinding.ActivityCodigoDeRecuperacaoBinding
import com.example.howdy.databinding.ActivityLoginBinding
import com.example.howdy.databinding.ActivityRecuperarSenhaBinding
import com.example.howdy.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth

class RecuperarSenha : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarSenhaBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarSenhaBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        val buttonCancelar = findViewById<Button>(R.id.button_cancelar)
        val buttonProximo = findViewById<Button>(R.id.button_proximo)

        auth = FirebaseAuth.getInstance()
        binding.buttonProximo.setOnClickListener { recuperacao() }

        buttonCancelar.setOnClickListener {
            val login =
                Intent(this, login::class.java)
            startActivity(login)
        }

    }

    private fun recuperacao() {

        val email = binding.textEmail.text.toString()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Email enviado!", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun recuperarsenha() {
        val intent = Intent(this, ActivityCodigoDeRecuperacaoBinding::class.java)
        startActivity(intent)
    }
}




