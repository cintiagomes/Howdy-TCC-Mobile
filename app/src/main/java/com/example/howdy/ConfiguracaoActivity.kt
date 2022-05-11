package com.example.howdy

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import com.example.howdy.model.MySqlResult
import com.example.howdy.model.UserTypes.User
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_configuracao.*
import kotlinx.android.synthetic.main.dialog_view.view.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header

const val CODE_IMAGEM = 100
const val CODE_FOTO = 110

class ConfiguracaoActivity : AppCompatActivity() {

    var imageBitmap : Bitmap? = null

    private lateinit var routerInterface: RouterInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_configuracao)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button)
        val buttonExcluir = findViewById<Button>(R.id.buttonExcluir)
        val tvtTrocarFoto = findViewById<ImageButton>(R.id.tvt_trocar_foto)
        val tvtTrocarFotoUser = findViewById<ImageButton>(R.id.tvt_trocar_foto_user)
        val textNome = findViewById<TextInputEditText>(R.id.textNome)

        //Caso a imagem seja um vector asset
        imageBitmap =
            resources
                .getDrawable(R.drawable.ic_pessoa)
                .toBitmap()

        tvtTrocarFoto.setOnClickListener {
            carregarGaleria()
        }

        tvtTrocarFotoUser.setOnClickListener {
            abrirGaleria()
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, imagem: Intent?) {
        super.onActivityResult(requestCode, resultCode, imagem)

        if (requestCode == CODE_IMAGEM && resultCode == -1){
            //Recuperar a imagem do stream(fluxo)
            val fluxoImagem = contentResolver.openInputStream(imagem!!.data!!)

            //Converter os bits em um bitmap
            imageBitmap = BitmapFactory.decodeStream(fluxoImagem)

            //Colocar o bitmap no ImageView
            userImage.setImageBitmap(imageBitmap)

        }
        if (requestCode == CODE_FOTO && resultCode == -1){

            //Recuperar a imagem do stream(fluxo)
            val fluxoImagem = contentResolver.openInputStream(imagem!!.data!!)

            //Converter os bits em um bitmap
            imageBitmap = BitmapFactory.decodeStream(fluxoImagem)

            userBackground.setImageBitmap(imageBitmap)

        }

    }

    private fun abrirGaleria() {

        //Abrir a galeria de imagens do dispositivo
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        //Abrir a Activity responsável por exibir as imagens
        //esta activity retornará o conteúdo selecionado
        //para o nosso app
        startActivityForResult(
            Intent.createChooser(intent,
                "Escolha uma foto"),
            CODE_IMAGEM
        )

    }

    private fun carregarGaleria() {

        //Abrir a galeria de imagens do dispositivo
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        //Abrir a Activity responsável por exibir as imagens
        //esta activity retornará o conteúdo selecionado
        //para o nosso app
        startActivityForResult(
            Intent.createChooser(intent,
                "Escolha uma foto"),
            CODE_FOTO
        )

    }
}