package com.example.howdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_view.view.*

class ConfiguracaoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracao)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button)
        val buttonExcluir = findViewById<Button>(R.id.buttonExcluir)

        arrowButton.setOnClickListener{ finish() }

//        buttonExcluir.setOnClickListener{
//
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("EXCLUIR CONTA")
//            builder.setMessage("Deseja Excluir mesmo a conta?")
//            builder.setNegativeButton("EXCLUIR"){ dialog, with ->
//                Toast.makeText(this,"Houve um erro no login", Toast.LENGTH_LONG).show()
//            }
//            builder.setNeutralButton("CANCELAR"){dialog, with ->
//                val configuracoes =
//                    Intent(this, ConfiguracaoActivity::class.java)
//                startActivity(configuracoes)
//            }
//            val dialog: AlertDialog = builder.create()
//            dialog.show()
//
//        }

        buttonExcluir.setOnClickListener{

            val view = View.inflate(this@ConfiguracaoActivity, R.layout.dialog_view, null)
            val builder = AlertDialog.Builder(this@ConfiguracaoActivity)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)

            view.bnt_cancelar.setOnClickListener {
                dialog.dismiss()
            }

            view.bnt_excluir.setOnClickListener {
                dialog.dismiss()
            }

        }

    }
}