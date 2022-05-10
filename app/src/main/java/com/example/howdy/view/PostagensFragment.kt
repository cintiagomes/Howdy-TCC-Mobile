package com.example.howdy.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.howdy.R
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.model.PostTypes.PostWithoutCreator
import com.example.howdy.model.UserTypes.UserCollectedWithId
import com.example.howdy.model.UserTypes.UserCreator
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.adapter.PostsAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostagensFragment(user: UserCollectedWithId) : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth
    val user = user

    override fun onCreate(savedInstanceState: Bundle?) {
        //LISTANDO AS POSTAGENS PELA PRIMEIRA VEZ COM AS CONFIGURAÇÕES PADRÃO (POSTAGENS POPULARES)
        findAndListUserPosts(user)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_postagens, container, false)
    }

    private fun findAndListUserPosts(user: UserCollectedWithId) {
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token

                if (idToken != null) {
                    //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                    routerInterface = APIUtil.`interface`

                    val call: Call<List<PostWithoutCreator>> = routerInterface.getUserPosts(user.idUser, idToken)
                    call.enqueue(object : Callback<List<PostWithoutCreator>> {
                        override fun onResponse(call: Call<List<PostWithoutCreator>>, response: Response<List<PostWithoutCreator>>) {
                            if (response.isSuccessful) {
                                /** RECEBER OS DADOS DA API  */
                                var postListWithoutCreatorList: List<PostWithoutCreator> = response.body()!!
                                var postWithCreatorList: MutableList<Post> = emptyList<Post>().toMutableList()

                                val userCreator = UserCreator(
                                    user.idUser,
                                    user.profilePhoto,
                                    user.userName,
                                    user.isPro,
                                    user.totalXp,
                                    user.patent
                                )


                                for (i in postListWithoutCreatorList.indices) {
                                    val postFormatted = Post(
                                        userCreator,
                                        postListWithoutCreatorList[i].idPost,
                                        postListWithoutCreatorList[i].imageContent,
                                        postListWithoutCreatorList[i].createdAt,
                                        postListWithoutCreatorList[i].textContent,
                                        null,
                                        postListWithoutCreatorList[i].isPublic,
                                        postListWithoutCreatorList[i].idTargetLanguage,
                                        postListWithoutCreatorList[i].idPostCategory,
                                        postListWithoutCreatorList[i].targetLanguageName,
                                        postListWithoutCreatorList[i].targetLanguageTranslatorName,
                                        postListWithoutCreatorList[i].categoryName,
                                        postListWithoutCreatorList[i].postCategoryIconImage,
                                        postListWithoutCreatorList[i].hexadecimalColor,
                                        postListWithoutCreatorList[i].totalLikes,
                                        postListWithoutCreatorList[i].liked,
                                        postListWithoutCreatorList[i].totalComments
                                    )

                                    postWithCreatorList.add(postFormatted)
                                }


                                val adapter = PostsAdapter(postWithCreatorList, activity!!)
                                val recycler = recycler_user_posts
                                recycler?.adapter = adapter
                            } else {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jObjError.get("error").toString()
                                if (errorMessage == "No posts found") {
                                    Toast.makeText(
                                        activity, "Nenhuma postagem foi encontrada",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val adapter = PostsAdapter(emptyList(), activity!!)
                                    val recycler = recycler_user_posts
                                    recycler?.adapter = adapter
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<PostWithoutCreator>>, t: Throwable) {
                            Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                                Toast.LENGTH_LONG).show()
                            println("DEBUGANDO - ONFAILURE NA LISTAGEM DE POSTS: $t")
                        }
                    })
                }
            }
    }
}