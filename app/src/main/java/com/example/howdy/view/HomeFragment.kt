package com.example.howdy.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
    private lateinit var popularButton: TextView
    private lateinit var friendsButton: TextView
    private lateinit var doubtsButton: TextView
    private lateinit var sportsButton: TextView
    private lateinit var newsButton: TextView
    private var currentlyCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //LISTANDO AS POSTAGENS PELA PRIMEIRA VEZ COM AS CONFIGURAÇÕES PADRÃO (POSTAGENS POPULARES)
        findAndListPosts("popular")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        popularButton = tv_popular_button
        friendsButton = tv_friends_button
        doubtsButton = tv_doubts_button
        sportsButton = tv_sports_button
        newsButton = tv_news_button

        //COLOCANDO UM OUVINTE EM CADA BOTÃO, PARA QUE A CATEGORIA DAS POSTAGENS SEJA ALTERADA
        putEventListenerInCategoryButtons()
    }

    private fun putEventListenerInCategoryButtons(){
        popularButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                popularButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_popular_background)

                findAndListPosts("popular")
            }
        }

        friendsButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                friendsButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_friends_background)

                findAndListPosts("myFriends")
            }
        }

        doubtsButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                doubtsButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_doubts_background)

                findAndListPosts("6")
            }
        }

        sportsButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                sportsButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_sports_background)

                findAndListPosts("1")
            }
        }

        newsButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                newsButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_news_background)

                findAndListPosts("2")
            }
        }
    }

    private fun unselectAllButtonsCategory(){
        popularButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_popular_background)

        friendsButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_friends_background)

        doubtsButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_doubts_background)

        sportsButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_sports_background)

        newsButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_news_background)
    }


    private fun findAndListPosts(categorySelected: String) {
        if (categorySelected == currentlyCategory)
            return Toast.makeText(
                        activity,"Você já está nessa categoria.",
                        Toast.LENGTH_LONG
                    ).show()

        currentlyCategory = categorySelected

        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token

                if (idToken != null) {
                    //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                    routerInterface = APIUtil.getInterface()

                    val call: Call<List<Post>> = routerInterface.getPublicPosts(categorySelected, idToken)
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

                                    val adapter = PostsAdapter(emptyList(), activity!!)
                                    val recycler = recycler_postagens
                                    recycler?.adapter = adapter
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
            }
    }
}