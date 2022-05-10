package com.example.howdy

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.PostsAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingFragment(rankingType: String, fragmentActivity:FragmentActivity) : Fragment() {
    private val rankingType = rankingType
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private lateinit var rankingTitleView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rankingTitleView = tv_ranking_title

        val initialTitleText = "Ranking de ganho de XP "
        val rankingTypeInPortuguese: String

        if(rankingType == "weekly") rankingTypeInPortuguese = "semanal"
        else if(rankingType == "monthly") rankingTypeInPortuguese = "mensal"
        else rankingTypeInPortuguese = "total"

        rankingTitleView.text = "$initialTitleText $rankingTypeInPortuguese."
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //LISTANDO AS POSTAGENS PELA PRIMEIRA VEZ COM AS CONFIGURAÇÕES PADRÃO (POSTAGENS POPULARES)
//        findAndListRanking()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

//    private fun findAndListRanking() {
//        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
//        auth = FirebaseAuth.getInstance()
//        auth.currentUser?.getIdToken(true)
//            ?.addOnSuccessListener { result ->
//                val idToken = result.token
//                println("DEBUGANDO IDTOKEN: $idToken")
//
//                if (idToken != null) {
//                    //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
//                    routerInterface = APIUtil.`interface`
//
//                    val call: Call<List<Post>> = routerInterface.getPublicPosts(categorySelected, idToken)
//                    call.enqueue(object : Callback<List<Post>> {
//                        override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
//                            if (response.isSuccessful) {
//                                /** RECEBER OS DADOS DA API  */
//                                var postList: List<Post> = response.body()!!
//                                val adapter = PostsAdapter(postList, activity!!)
//                                val recycler = recycler_user_posts
//                                recycler?.adapter = adapter
//                            } else {
//                                val jObjError = JSONObject(response.errorBody()!!.string())
//                                val errorMessage = jObjError.get("error").toString()
//                                if (errorMessage == "No posts found") {
//                                    Toast.makeText(
//                                        activity, "Nenhuma postagem foi encontrada",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//
//                                    val adapter = PostsAdapter(emptyList(), activity!!)
//                                    val recycler = recycler_user_posts
//                                    recycler?.adapter = adapter
//                                }
//                            }
//                        }
//
//                        override fun onFailure(call: Call<List<Post>>, t: Throwable) {
//                            Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
//                                Toast.LENGTH_LONG).show()
//                            println("DEBUGANDO - ONFAILURE NA LISTAGEM DE POSTS: $t")
//                        }
//                    })
//                }
//            }
//    }

}