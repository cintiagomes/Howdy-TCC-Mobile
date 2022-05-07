package com.example.howdy.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.MySqlResult
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.model.TraductionTypes.DataToTranslate
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsAdapter(private val posts: List<Post>, activity: FragmentActivity) : RecyclerView.Adapter<PostsAdapter.Holder>() {
    private val activity = activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = if (viewType == 0){
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_with_image, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_without_image, parent, false)
        }
        return HomeViewHolder(itemView, activity)
    }

    override fun getItemViewType(position: Int): Int {
        if (posts[position]?.imageContent != null){
            return 0
        }
        return 1
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(obj: Post)
    }

    class HomeViewHolder(itemView: View, activity: FragmentActivity) : Holder(itemView) {
        private val activity = activity

        private val auth = FirebaseAuth.getInstance()
        private val routerInterface: RouterInterface = APIUtil.getInterface()

        private val userCreatorNameView: TextView = itemView.findViewById(R.id.user_creator_name_view)
        private val userCreatorProfilePhotoView: ImageView = itemView.findViewById(R.id.user_creator_profile_photo_view)
        private val textContentView: TextView = itemView.findViewById(R.id.text_content_view)
        private val createdAtView: TextView = itemView.findViewById(R.id.created_at_view)
        private val totalCommentsView: TextView = itemView.findViewById(R.id.total_comments_view)
        private val totalLikesView: TextView = itemView.findViewById(R.id.total_likes_view)
        private val imageContentView: ImageView? = itemView.findViewById(R.id.iv_image_content)
        private val likeButtonView: ImageView = itemView.findViewById(R.id.iv_like_button)
        private val traductionButtonView: ImageButton = itemView.findViewById(R.id.btn_traduct)

        override fun bind(post: Post) {
            userCreatorNameView.text = post.userCreator.userName
            //BUSCANDO A IMAGEM DO USUÁRIO ATRAVÉS DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
            if(post.userCreator?.profilePhoto != null) {
                Glide
                    .with(userCreatorProfilePhotoView)
                    .load(post.userCreator.profilePhoto)
                    .into(userCreatorProfilePhotoView);
            }
            textContentView.text = post.textContent
            createdAtView.text = post.createdAt
            totalCommentsView.text = post.totalComments.toString()
            totalLikesView.text = post.totalLikes.toString()
            //BUSCANDO A IMAGEM DA POSTAGEM ATRAVÉS DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
            println(imageContentView)
            if(imageContentView != null) {
                Glide
                    .with(imageContentView)
                    .load(post.imageContent)
                    .into(imageContentView);
            }

            //MARCANDO CURTIDA, CASO O USUÁRIO JÁ TENHA COMENTADO NESSA POSTAGEM
            if(post.liked){
                likeButtonView.setImageDrawable(
                    ContextCompat.getDrawable(
                    activity,
                    R.drawable.ic_filled_heart_24))
            }

            //QUANDO O USUÁRIO CLICAR NO BOTÃO DE TRADUÇÃO, A POSTAGEM SERÁ TRADUZIDA PARA SEU IDIOMA NATIVO
            traductionButtonView.setOnClickListener { handleTraduct(post) }

            //QUANDO O USUÁRIO CLICAR NO BOTÃO DE LIKE, PODERÁ CURTIR, OU DESCUTIR UMA POSTAGEM
            likeButtonView.setOnClickListener { handleLikeOrUnlike(post) }
        }
        fun handleTraduct(post: Post){
            //VERIFICANDO SE O TEXTO QUE ESTÁ ESCRITO JÁ É UMA TRADUÇÃO
            if (textContentView.text == post.textContent){
                //VERIFICANDO SE ESTE POST JÁ POSSUI UMA TRADUÇÃO
                if (post.translatedTextContent == null) {
                    translateText(post)
                } else {
                    textContentView.text = post.translatedTextContent
                }
            } else {
                textContentView.text = post.textContent
            }
        }

        fun translateText(post: Post){
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
                            arrayListOf<String>(post.textContent)
                        )

                        val call: Call<List<String>> = routerInterface.traductText(idToken, dataToTranslate)
                        call.enqueue(object : Callback<List<String>> {
                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                if (response.isSuccessful) {
                                    post.translatedTextContent = response.body()?.get(0).toString()
                                    textContentView.text = post.translatedTextContent
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

        fun handleLikeOrUnlike(post: Post){
//            RESGATANDO IDTOKEN DO USUÁRIO LOGADO
            auth.currentUser?.getIdToken(true)
                ?.addOnSuccessListener { result ->
                    val idToken = result.token

                    if (idToken != null) {
                        //VERIFICANDO SE O USUÁRIO IRÁ CURTIR, OU DESCURTIR A POSTAGEM
                            var call:Call<MySqlResult>? = null
                            var wasLiked:Boolean = false
                            if (post.liked){
                                call = routerInterface.unlikePost(idToken, post.idPost)
                            } else {
                                wasLiked = true
                                call = routerInterface.likePost(idToken, post.idPost)
                            }

                        //INTERCEPTANDO RESPOSTA DA CURTIDA
                        call.enqueue(object : Callback<MySqlResult> {
                            override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                                if (response.isSuccessful) {
                                    if (wasLiked){
                                        post.totalLikes++
                                        post.liked = true
                                        totalLikesView.text = (totalLikesView.text.toString().toInt() + 1).toString()
                                        likeButtonView.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                activity,
                                                R.drawable.ic_filled_heart_24))
                                    }else{
                                        post.totalLikes--
                                        post.liked = false
                                        totalLikesView.text = (totalLikesView.text.toString().toInt() - 1).toString()
                                        likeButtonView.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                activity,
                                                R.drawable.ic_unfilled_heart_24))
                                    }
                                } else {
                                    Toast.makeText(
                                        activity, "Ocorreu um erro, tente novamente.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                                Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                                    Toast.LENGTH_LONG).show()
                                println("DEBUGANDO - ONFAILURE NA CURTIDA OU DESCURTIDA: $t")
                            }
                        })
                    }
                }
        }
    }
}