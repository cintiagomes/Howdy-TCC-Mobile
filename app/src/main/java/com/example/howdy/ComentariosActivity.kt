package com.example.howdy

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.howdy.adapter.CommentaryItemAdapter
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_comentarios.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComentariosActivity : AppCompatActivity() {
    private lateinit var routerInterface: RouterInterface
    private var auth = FirebaseAuth.getInstance()
    private lateinit var commentaryList: MutableList<Commentary>

    private var context = this

    private lateinit var commentsQuantityView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_comentarios)

        commentsQuantityView = total_comments_view

        arrow_button.setOnClickListener{ finish() }

        //RESGATANDO IDPOST DE EXTRAS DA ACTIVITY
        val idPost = intent.getIntExtra("idPost", 0)

        findAndListComments(idPost)
    }

    private fun findAndListComments(idPost: Int){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token
                println("DEBUGANDO IDTOKEN: $idToken")

                if (idToken != null) {
                    //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                    routerInterface = APIUtil.`interface`

                    val call: Call<List<Commentary>> = routerInterface.getPostComments(idToken, idPost)
                    call.enqueue(object : Callback<List<Commentary>> {
                        override fun onResponse(call: Call<List<Commentary>>, response: Response<List<Commentary>>) {
                            if (response.isSuccessful) {
                                /** RECEBER OS DADOS DA API  */
                                commentaryList = (response.body() as MutableList<Commentary>?)!!
                                val adapter = CommentaryItemAdapter(commentaryList, context!!)
                                val recycler = recyclerView
                                recycler?.adapter = adapter
                            } else {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jObjError.get("error").toString()
                                if (errorMessage == "No comments found") {
                                    Toast.makeText(
                                        context, "Nenhum comentário foi encontrado",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val adapter = CommentaryItemAdapter(emptyList(), context!!)
                                    val recycler = recyclerView
                                    recycler?.adapter = adapter
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Commentary>>, t: Throwable) {
                            Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                                Toast.LENGTH_LONG).show()
                            println("DEBUGANDO - ONFAILURE NA LISTAGEM DE COMENTÁRIOS: $t")
                        }
                    })
                }
            }
    }



}