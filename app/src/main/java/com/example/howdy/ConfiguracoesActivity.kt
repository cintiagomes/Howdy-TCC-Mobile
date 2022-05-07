package com.example.howdy

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.howdy.view.paginaDePostagem
import kotlinx.android.synthetic.main.activity_recuperar_senha.*

class ConfiguracoesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_configuracoes)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button)
        val buttonExcluir = findViewById<Button>(R.id.buttonExcluir)

        arrowButton.setOnClickListener{ finish() }

        buttonExcluir.setOnClickListener{

            val builder = AlertDialog.Builder(this, R.style.excluir)
            builder.setTitle("EXCLUIR CONTA")
            builder.setMessage("Deseja Excluir mesmo a conta?")
            builder.setNegativeButton("EXCLUIR"){ dialog, with ->
                Toast.makeText(this,"Houve um erro no login", Toast.LENGTH_LONG).show()
            }
            builder.setNeutralButton("CANCELAR"){dialog, with ->
                val configuracoes =
                    Intent(this, ConfiguracoesActivity::class.java)
                startActivity(configuracoes)
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

    }

}