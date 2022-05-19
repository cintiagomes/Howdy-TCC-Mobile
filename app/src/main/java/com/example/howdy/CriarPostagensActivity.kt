package com.example.howdy

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.example.howdy.databinding.ActivityCriarPostagensBinding
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comentarios.arrow_button
import kotlinx.android.synthetic.main.activity_criar_postagens.*
import org.w3c.dom.Text

const val CODE_IMAGE = 100

class CriarPostagensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriarPostagensBinding
    private lateinit var auth: FirebaseAuth
    var imageBitmap : Bitmap? = null

    private lateinit var userNameView: TextView
    private lateinit var userProfilePhotoView: CircleImageView
    private lateinit var visibilityView: TextView
    private lateinit var postCategoryView: TextView
    private lateinit var buttonPost: Button
    private lateinit var textContentView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarPostagensBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        arrow_button.setOnClickListener{ finish() }

        userNameView = user_creator_name_comment
        userProfilePhotoView = user_creator_photo_comment
        visibilityView = select_visibilidade
        postCategoryView = select_categoria
        buttonPost = btnPost
        textContentView = tv_text_content

        //RESGATA O USERNAME, E A FOTO DE PERFIL DO USUÁRIO LOGADO NO SHAREDPREFERENCES
        val sharedPreferences = getSharedPreferences("userLogged", 0)
        val userName = sharedPreferences.getString("userName", "")
        val profilePhoto = sharedPreferences.getString("profilePhoto", "")

        userNameView.text = userName

        //RENDERIZANDO A FOTO DE PERFIL, SE HOUVER
        if (profilePhoto?.length!! > 0) {
            Glide
                .with(userProfilePhotoView)
                .load(profilePhoto)
                .into(userProfilePhotoView);
        }

        imageBitmap = resources.getDrawable(R.drawable.ic_add_image_24).toBitmap()

        add_image.setOnClickListener {
            openGallery()
        }

        val visibilities = resources.getStringArray(R.array.visibilidades)
        val adapterVisibilities = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            visibilities
        )

        val postCategories = resources.getStringArray(R.array.categorias_de_post)
        val adapterPostCategories = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            postCategories
        )

        with(binding.selectVisibilidade){
            setAdapter(adapterVisibilities)
        }

        with(binding.selectCategoria){
            setAdapter(adapterPostCategories)
        }

        btnPost.setOnClickListener { post() }
    }

    private fun post(){
        if (textContentView.text.isNullOrEmpty()) return Toast.makeText(this, "Escreva algo antes de postar", Toast.LENGTH_SHORT).show()
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