package com.example.howdy.view

import android.content.ClipData.Item
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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
            private val textContentView: TextView = itemView.findViewById(R.id.text_content_view)
            private val createdAtView: TextView = itemView.findViewById(R.id.created_at_view)
            private val totalCommentsView: TextView = itemView.findViewById(R.id.total_comments_view)
            private val totalLikesView: TextView = itemView.findViewById(R.id.total_likes_view)

            override fun bind(post: Post) {
                userCreatorNameView.text = post.userCreator.userName
                textContentView.text = post.textContent
                createdAtView.text = post.createdAt
                totalCommentsView.text = post.totalComments.toString()
                totalLikesView.text = post.totalLikes.toString()

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


    fun findAndListPosts(idToken: String){
        println("DEBUGANDO IDTOKEN:" + idToken)

        val thisContext = this
        routerInterface = APIUtil.getInterface()

        println("DEBUGANDO ANTES DA QUERY")

        val call: Call<List<Post>> = routerInterface.getPublicPosts("populars", idToken)

        println("DEBUGANDO ANTES DE INTERCEPTORS")
        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    println("DEBUGANDO SUCESSO")
                    val itens: MutableList<Item> = ArrayList()

                    /** RECEBER OS DADOS DA API  */
                    var postList: List<Post> = response.body()!!
                    val adapter = PostsAdapter(postList)
                    val recycler = recycler_postagens
                    recycler?.adapter = adapter
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.get("error").toString()
                    //Toast.makeText()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                println("DEBUGANDO: ONFAILURE NA LISTAGEM DE POSTS")
            }
        })
    }

}