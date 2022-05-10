package com.example.howdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.example.howdy.model.MySqlResult
import com.example.howdy.model.UserTypes.User
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import kotlinx.android.synthetic.main.dialog_view.view.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header

class ConfiguracaoActivity : AppCompatActivity() {

    private lateinit var routerInterface: RouterInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
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
                routerInterface = APIUtil.`interface`
//                val call: Call<MySqlResult> = routerInterface.deleteMyAccountOnBackend()


                dialog.dismiss()
            }

        }

    }
}