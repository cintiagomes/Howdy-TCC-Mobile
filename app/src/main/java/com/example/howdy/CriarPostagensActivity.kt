package com.example.howdy

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.example.howdy.adapter.CommentaryItemAdapter
import com.example.howdy.databinding.ActivityCriarPostagensBinding
import com.example.howdy.model.MySqlResult
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comentarios.*
import kotlinx.android.synthetic.main.activity_comentarios.arrow_button
import kotlinx.android.synthetic.main.activity_criar_postagens.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

const val CODE_IMAGE = 100

class CriarPostagensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriarPostagensBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var routerInterface: RouterInterface
    var imageBitmap : Bitmap? = null

    private lateinit var userNameView: TextView
    private lateinit var userProfilePhotoView: CircleImageView
    private lateinit var visibilityView: TextView
    private lateinit var postCategoryView: TextView
    private lateinit var buttonPost: Button
    private lateinit var textContentView: TextView

    private var postImageFile: File? = null
    private var postImageFormData: MultipartBody.Part? = null
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarPostagensBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        arrow_button.setOnClickListener{ finish() }
        routerInterface = APIUtil.`interface`

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
        if (binding.selectVisibilidade.text.isNullOrEmpty()) return Toast.makeText(this, "Você deve selecionar uma visibilidade", Toast.LENGTH_SHORT).show()
        if (binding.selectCategoria.text.isNullOrEmpty()) return Toast.makeText(this, "Você deve selecionar uma categoria", Toast.LENGTH_SHORT).show()

        createPost()
    }

    private fun createPost() {
        //CONVERTENDO A IMAGEM DO POST PARA MultipartBody, SE HOUVER
        if (postImageFile != null) {
            val requestFile: RequestBody =
                RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    postImageFile!!
                )

            postImageFormData = MultipartBody.Part.createFormData(
                "imageContent",
                postImageFile!!.name,
                requestFile
            )
        }

        //CONVERTENDO O TEXTO DO POST PARA MultipartBody
        val requestText: RequestBody =
            RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                textContentView.text.toString()
            )

        //CONVERTENDO A VISIBILIDADE DO POST PARA MultipartBody
        val requestVisibility = RequestBody.create("text/plain".toMediaTypeOrNull(), (binding.selectVisibilidade.text.toString() == "Público").toString() )


        //COLOCANDO DETERMINADO ID A DEPENDER DO VALOR DA CATEGORIA
        val idCategory = when(binding.selectCategoria.text.toString()){
            "Esportes" -> 1
            "Notícias" -> 2
            "Jogos" -> 3
            "Filmes" -> 4
            "Moda" -> 5
            "Dúvidas" -> 6
            else -> 0
        }

        //CONVERTENDO A CATEGORIA DO POST PARA MultipartBody
        val requestCategory = RequestBody.create("text/plain".toMediaTypeOrNull(), idCategory.toString())

        //RESGATANDO IDTOKEN DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!

                val call: Call<MySqlResult> = routerInterface.createPost(requestText, requestVisibility, requestCategory, idToken, postImageFormData)

                call.enqueue(object : Callback<MySqlResult> {
                    override fun onResponse(
                        call: Call<MySqlResult>,
                        response: Response<MySqlResult>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Post criado com sucesso!",
                                Toast.LENGTH_LONG
                            ).show()

                            //ENCERRANDO ESTA ACTIVITY
                            finish()
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            if (errorMessage == "The text is not written according to the language you want to learn") {
                                return Toast.makeText(
                                    context, "A postagem deve estar escrita conforme o idioma de seu interesse",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            Toast.makeText(
                                context,
                                "Ocorreu um erro, tente novamente mais tarde.",
                                Toast.LENGTH_LONG
                            ).show()

                            println("DEBUGANDO - ONFAILURE NA CRIAÇÃO DE POSTS: $errorMessage")
                        }
                    }

                    override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                        Toast.makeText(
                            context,
                            "Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG
                        ).show()
                        println("DEBUGANDO - ONFAILURE NA CRIAÇÃO DE POSTS: $t")
                    }
                })
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, image: Intent?) {
        super.onActivityResult(requestCode, resultCode, image)

        if (requestCode == CODE_IMAGE && resultCode == -1){
            val fluxoImage = contentResolver.openInputStream(image!!.data!!)
            imageBitmap = BitmapFactory.decodeStream(fluxoImage)
            add_image.setImageBitmap(imageBitmap)

            //CONVERTENDO A IMAGEM NO FORMATO BITMAP PARA FILE
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image$requestCode.jpg")
            file.createNewFile()

            val bos = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            postImageFile = file
        }

    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Escolha uma foto"), CODE_IMAGE)
    }
}