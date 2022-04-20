
package com.example.howdy

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.databinding.ActivityCadastroBinding
import com.example.howdy.http.HttpHelper
import com.example.howdy.view.paginaDePostagem
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
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

        val textRegistrar = findViewById<TextView>(R.id.link_registar)

        auth = FirebaseAuth.getInstance()
        binding.buttonCadastrar.setOnClickListener { cadastrar() }

        textRegistrar.setOnClickListener {
            val login =
                Intent(this, com.example.howdy.view.login::class.java)
            startActivity(login)
        }

    }

    private fun cadastrar() {
        val userName = binding.textNome.text.toString()
        val birthDate = binding.textData.text.toString()
        val email = binding.textEmail.text.toString()
        val nativeLanguage = binding.selectIdiomaNativo.text.toString()
        //val targetLanguage = binding.selectIdiomaInteresse.text.toString()
        val senha = binding.textSenha.text.toString()

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener{
            if (it.isSuccessful){
                //RESGATANDO IDTOKEN DO USUÁRIO LOGADO NO FIREBASE
                auth.currentUser?.getIdToken(true)
                    ?.addOnSuccessListener(OnSuccessListener<GetTokenResult> { result ->
                        val idToken = result.token
                        if (idToken != null){
                        //CADASTRANDO O USUÁRIO NO BANCO SQL
                            val http = HttpHelper()
                            //val res = http.post("/users", idToken, json)

                        Toast.makeText(applicationContext,"Cadastro realizado com sucesso!",
                            Toast.LENGTH_LONG).show()
                        navigateToPostPage()
                        }
                    })
            }else{
                Toast.makeText(applicationContext,"Houve um erro no seu cadastro",
                Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToPostPage() {
        val targetPage = Intent(this, paginaDePostagem::class.java)
        startActivity(targetPage)
    }
}