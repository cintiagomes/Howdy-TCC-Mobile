package com.example.howdy.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val itens = arrayListOf(
//            0 to "item A",
//            0 to "item B",
//            0 to "item C"
//        )
//
//        val adapter = HomeAdapter(itens)
//        println("DEBUGANDO: $view")
//        val recycler = recycler_postagens
//        recycler?.adapter = adapter

        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token

                if (idToken != null) {
                    //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                        println("DEBUGANDO LOGOU")
                    findAndListPosts(idToken)
                }
            }
    }

    class PostsAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostsAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_postagens, parent, false)
            return HomeViewHolder(itemView)
        }

        fun hasPostAnImage(position: Int): Boolean {
            return posts[position]?.imageContent?.length!! > 0

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

        class HomeViewHolder(itemView: View) : Holder(itemView) {
            private val userCreatorNameView: TextView = itemView.findViewById(R.id.user_creator_name_view)
            private val userCreatorProfilePhotoView: ImageView = itemView.findViewById(R.id.user_creator_profile_photo_view)
            private val textContentView: TextView = itemView.findViewById(R.id.text_content_view)
            private val createdAtView: TextView = itemView.findViewById(R.id.created_at_view)
            private val totalCommentsView: TextView = itemView.findViewById(R.id.total_comments_view)
            private val totalLikesView: TextView = itemView.findViewById(R.id.total_likes_view)
            private val imageContentView: ImageView = itemView.findViewById(R.id.iv_image_content)

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
                if(post?.imageContent != null) {
                    Glide
                        .with(imageContentView)
                        .load(post.imageContent)
                        .into(imageContentView);
                }
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    private fun findAndListPosts(idToken: String){
        println("DEBUGANDO IDTOKEN:" + idToken)

        val thisContext = this
        routerInterface = APIUtil.getInterface()

        println("DEBUGANDO ANTES DA QUERY")

        val call: Call<List<Post>> = routerInterface.getPublicPosts("popular", idToken)

        println("DEBUGANDO ANTES DE INTERCEPTORS")
        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    /** RECEBER OS DADOS DA API  */
                    var postList: List<Post> = response.body()!!
                    val adapter = PostsAdapter(postList)
                    val recycler = recycler_postagens
                    recycler?.adapter = adapter
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.get("error").toString()
                    if (errorMessage == "No posts found") {
                        Toast.makeText(
                            activity, "Nenhuma atividade foi encontrada",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                    Toast.LENGTH_LONG).show()
                println("DEBUGANDO - ONFAILURE NA LISTAGEM DE POSTS: $t")
            }
        })
    }

//    fun putImageUrlInImageView(urlImage: String) : Boolean {
//        Glide.with(this)
//            .load(urlImages.get(urlImage))
//            .asBitmap()
//            .listener(new RequestListener<String, Bitmap>() {
//                @Override
//                public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFirstResource) {
//                    // AÇÕES A SE EXECUTAR CASO HOUVER ERRO
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Bitmap resource, String model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                    // AÇÕES A SE EXECUTAR QUANDO FOR CARREGADA A IMAGEM
//                    return false;
//                }
//            }).into(SUA IMAGEVIEW);
//    }

}