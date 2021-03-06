package com.example.howdy.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.howdy.CriarPostagensActivity
import com.example.howdy.R
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.adapter.PostsAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private lateinit var userLoggedProfilePhotoView: ImageView
    private lateinit var createPostBox: LinearLayout
    private lateinit var popularButton: TextView
    private lateinit var friendsButton: TextView
    private lateinit var doubtsButton: TextView
    private lateinit var sportsButton: TextView
    private lateinit var newsButton: TextView
    private lateinit var gamesButton: TextView
    private lateinit var moviesButton: TextView
    private lateinit var modaButton: TextView

    private var currentCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userLoggedProfilePhotoView = iv_user_logged_profile_photo
        createPostBox = create_post_box
        println("DEBUGANDO")
        println(createPostBox)
        popularButton = tv_popular_button
        friendsButton = tv_friends_button
        doubtsButton = tv_doubts_button
        sportsButton = tv_sports_button
        newsButton = tv_news_button
        gamesButton = tv_games_button
        moviesButton = tv_movies_button
        modaButton = tv_moda_button

        /** SUBSTITUINDO IMAGEM PADR??O PELA FOTO DE PERFIL DO USU??RIO **/
        //RESGATANDO FOTO DE PERFIL DO USU??RIO LOGADO
        val userLoggedFile = requireActivity().getSharedPreferences(
            "userLogged", Context.MODE_PRIVATE)

        val profilePhoto = userLoggedFile.getString("profilePhoto", "")

        if (profilePhoto?.length!! > 0) {
            Glide
                .with(userLoggedProfilePhotoView)
                .load(profilePhoto)
                .into(userLoggedProfilePhotoView);
        }

        findAndListPosts("popular")

        //COLOCANDO UM OUVINTE EM CADA BOT??O, PARA QUE A CATEGORIA DAS POSTAGENS SEJA ALTERADA
        putEventListenerInButtons()
    }

    private fun putEventListenerInButtons(){
        createPostBox.setOnClickListener { view ->
            run {
                val intent = Intent(requireActivity(), CriarPostagensActivity::class.java)
                startActivity(intent)
            }
        }

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

        gamesButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                gamesButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_games_background)

                findAndListPosts("3")
            }
        }

        moviesButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                moviesButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_movies_background)

                findAndListPosts("4")
            }
        }

        modaButton.setOnClickListener { view ->
            run {
                unselectAllButtonsCategory()

                modaButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_moda_background)

                findAndListPosts("5")
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

        gamesButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_games_background)

        moviesButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_movies_background)

        modaButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_moda_background)
    }


    private fun findAndListPosts(categorySelected: String) {
        if (categorySelected == currentCategory)
            return Toast.makeText(
                        activity,"Voc?? j?? est?? nessa categoria.",
                        Toast.LENGTH_LONG
                    ).show()

        currentCategory = categorySelected

        //RESGATANDO IDTOKEN ATUAL DO USU??RIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token
                println("DEBUGANDO IDTOKEN: $idToken")

                if (idToken != null) {
                    //O USU??RIO EST?? LOGADO, E FAR?? A LISTAGEM DE POSTAGENS
                    routerInterface = APIUtil.`interface`

                    val call: Call<List<Post>> = routerInterface.getPublicPosts(categorySelected, idToken)
                    call.enqueue(object : Callback<List<Post>> {
                        override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                            if (response.isSuccessful) {
                                /** RECEBER OS DADOS DA API  */
                                var postList: List<Post> = response.body()!!
                                val adapter = PostsAdapter(postList, activity!!)
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

                        override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                            Toast.makeText(activity,"Houve um erro de conex??o, verifique se est?? conectado na internet.",
                                Toast.LENGTH_LONG).show()
                            println("DEBUGANDO - ONFAILURE NA LISTAGEM DE POSTS: $t")
                        }
                    })
                }
            }
    }
}