package com.example.howdy.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.PostsAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth
    private lateinit var activityContext: FragmentActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityContext = requireActivity()

        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token

                if (idToken != null) {
                    //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                    findAndListPosts(idToken)
                }
            }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    private fun findAndListPosts(idToken: String){
        println("DEBUGANDO IDTOKEN:" + idToken)

        val thisContext = this
        routerInterface = APIUtil.getInterface()

        val call: Call<List<Post>> = routerInterface.getPublicPosts("popular", idToken)
        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    /** RECEBER OS DADOS DA API  */
                    var postList: List<Post> = response.body()!!
                    val adapter = PostsAdapter(postList, activity!!)
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
//    }

}