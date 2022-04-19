
package com.example.howdy

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
<<<<<<< HEAD
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.databinding.ActivityCadastroBinding
=======
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.databinding.ActivityCadastroBinding
import com.example.howdy.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
>>>>>>> b528cdae738277d1fa01b8bd24b70fedb3d27d47
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        //Criação do calendario
        val calendario = Calendar.getInstance()

        //Determinar o dia mes e ano do calendario
        val ano =calendario.get(Calendar.YEAR)
        val mes =calendario.get(Calendar.MONTH)
        val dia =calendario.get(Calendar.DAY_OF_MONTH)

        //Pegando o id para abrir o componente DatePincker
        val etDataNascimento = findViewById<TextInputEditText>(R.id.textData)

        etDataNascimento.setOnClickListener {
            val data = DatePickerDialog(
                this, DatePickerDialog.OnDateSetListener{ view, _ano, _mes, _dia ->
                    etDataNascimento.setText("$_dia/${_mes + 1}/$_ano")
                }, ano, mes, dia)
            data.show()
        }

        val textRegistar = findViewById<TextView>(R.id.text_registar)

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

        textRegistar.setOnClickListener {
            val login =
                Intent(this, com.example.howdy.view.login::class.java)
            startActivity(login)
        }

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