package com.example.howdy

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.graphics.drawable.toBitmap
import com.example.howdy.databinding.ActivityCadastroBinding
import com.example.howdy.databinding.ActivityPostagensBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_comentarios.*
import kotlinx.android.synthetic.main.activity_comentarios.arrow_button
import kotlinx.android.synthetic.main.activity_postagens.*

const val CODE_IMAGE = 100

class PostagensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostagensBinding
    private lateinit var auth: FirebaseAuth
    var imageBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostagensBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        arrow_button.setOnClickListener{ finish() }

        imageBitmap = resources.getDrawable(R.drawable.ic_add_image_24).toBitmap()

        add_image.setOnClickListener {
            openGallery()
        }

        val countries = resources.getStringArray(R.array.linguagens)
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            countries
        )

        with(binding.selectCategoria){
            setAdapter(adapter)
        }

        with(binding.selectVisibilidade){
            setAdapter(adapter)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, image: Intent?) {
        super.onActivityResult(requestCode, resultCode, image)

        if (requestCode == CODE_IMAGE && resultCode == -1){
            val fluxoImage = contentResolver.openInputStream(image!!.data!!)
            imageBitmap = BitmapFactory.decodeStream(fluxoImage)
            add_image.setImageBitmap(imageBitmap)
        }

    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Escolha uma foto"), CODE_IMAGE)
    }
}