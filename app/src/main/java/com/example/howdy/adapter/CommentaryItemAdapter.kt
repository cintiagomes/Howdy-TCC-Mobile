package com.example.howdy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.model.TraductionTypes.DataToTranslate
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.view.PerfilActivity
import com.google.firebase.auth.FirebaseAuth
import convertBackEndDateTimeFormatToSocialMediaFormat
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentaryItemAdapter(private val comments: List<Commentary>, private val activity: FragmentActivity) : RecyclerView.Adapter<CommentaryItemAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_comentar, parent, false)

        return CommentaryViewHolder(itemView, activity)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(obj: Commentary)
    }

    class CommentaryViewHolder(itemView: View, private val activity: FragmentActivity) : Holder(itemView) {
        private val auth = FirebaseAuth.getInstance()
        private val routerInterface: RouterInterface = APIUtil.`interface`

        private val profilePhotoView: CircleImageView = itemView.findViewById(R.id.user_creator_photo_comment)
        private val userNameView: TextView = itemView.findViewById(R.id.user_creator_name_comment)
        private val patentView: ImageView = itemView.findViewById(R.id.iv_user_creator_patent)
        private val createdAtView: TextView = itemView.findViewById(R.id.created_at_comment)
        private val textContentView: TextView = itemView.findViewById(R.id.text_comment)
        private val traductionButtonView: ImageView = itemView.findViewById(R.id.btn_traduct)

        override fun bind(obj: Commentary) {
            userNameView.text = obj.commenter.userName

            //FORMATANDO DATA PARA INSERIR NA TELA
            val formattedCreatedAt = convertBackEndDateTimeFormatToSocialMediaFormat(obj.postCommentaryCreatedAt)
            createdAtView.text = formattedCreatedAt

            textContentView.text = obj.textCommentary

            //BUSCANDO A IMAGEM DO USUÁRIO ATRAVÉS DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
            if(obj.commenter.profilePhoto != null) {
                Glide
                    .with(profilePhotoView)
                    .load(obj.commenter.profilePhoto)
                    .into(profilePhotoView)
            }

            //EXIBINDO PATENTE DO USUÁRIO, SE ELE FOR PRO
            if(obj.commenter.patent != null){
                putPatentImage(obj.commenter.patent!!)
            }


            //IR PARA A PÁGINA DO CRIADOR DA POSTAGEM, QUANDO CLICAMOS EM SUA IMAGEM, OU NOME
            userNameView.setOnClickListener { goToUserActivity(obj.commenter.idUser) }
            profilePhotoView.setOnClickListener { goToUserActivity(obj.commenter.idUser) }

            //TRADUZIR TEXTO QUANDO CLICADO
            traductionButtonView.setOnClickListener { handleTraduct(obj) }
        }

        private fun handleTraduct(commentary: Commentary){
            //VERIFICANDO SE O TEXTO QUE ESTÁ ESCRITO JÁ É UMA TRADUÇÃO
            if (textContentView.text == commentary.textCommentary){
                //VERIFICANDO SE ESTE POST JÁ POSSUI UMA TRADUÇÃO
                if (commentary.translatedtTextCommentary == null) {
                    translateText(commentary)
                } else {
                    textContentView.text = commentary.translatedtTextCommentary
                }
            } else {
                textContentView.text = commentary.textCommentary
            }
        }

        private fun translateText(commentary: Commentary){
            //RESGATANDO IDIOMA NATIVO DO USUÁRIO LOGADO
            val userLoggedFile = activity.getSharedPreferences(
                "userLogged", Context.MODE_PRIVATE)

            val nativeLanguageTranslatorName = userLoggedFile.getString("nativeLanguageTranslatorName", "")

            //RESGATANDO IDTOKEN DO USUÁRIO LOGADO
            auth.currentUser?.getIdToken(true)
                ?.addOnSuccessListener { result ->
                    val idToken = result.token

                    if (idToken != null && nativeLanguageTranslatorName != null) {
                        //EFETIVAR REQUISIÇÃO DE TRADUÇÃO
                        val dataToTranslate = DataToTranslate(
                            nativeLanguageTranslatorName,
                            arrayListOf(commentary.textCommentary)
                        )

                        val call: Call<List<String>> = routerInterface.traductText(idToken, dataToTranslate)
                        call.enqueue(object : Callback<List<String>> {
                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                if (response.isSuccessful) {
                                    commentary.translatedtTextCommentary = response.body()?.get(0).toString()
                                    textContentView.text = commentary.translatedtTextCommentary
                                } else {
                                    Toast.makeText(
                                        activity, "Ocorreu um erro na tradução.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                                    Toast.LENGTH_LONG).show()
                                println("DEBUGANDO - ONFAILURE NA TRADUÇÃO: $t")
                            }
                        })
                    }
                }
        }

        private fun putPatentImage(patent: String){
            when (patent) {
                "noob" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_noob
                        )
                    )
                }
                "beginner" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_beginner
                        )
                    )
                }
                "amateur" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_amateur
                        )
                    )
                }
                "experient" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_experient
                        )
                    )
                }
                "veteran" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_veteran
                        )
                    )
                }
                else -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_master
                        )
                    )
                }
            }
        }

        private fun goToUserActivity(idUser:Int) {
            val targetPage = Intent(activity, PerfilActivity::class.java)

            targetPage.putExtra("idUser", idUser);
            activity.startActivity(targetPage)
        }
    }
}