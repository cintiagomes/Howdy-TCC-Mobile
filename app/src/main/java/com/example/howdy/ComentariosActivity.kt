package com.example.howdy

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.howdy.adapter.CommentaryItemAdapter
import com.example.howdy.model.MySqlResult
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.model.PostTypes.PostCommentaryTypes.DataToCreateCommentary
import com.example.howdy.model.UserTypes.Commenter
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.convertStringtoEditable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_comentarios.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ComentariosActivity : AppCompatActivity() {
    private lateinit var routerInterface: RouterInterface
    private var auth = FirebaseAuth.getInstance()
    private lateinit var commentaryList: MutableList<Commentary>
    private lateinit var idToken: String

    private var context = this

    private lateinit var commentsQuantityView: TextView
    private lateinit var inputCommentaryView: EditText
    private lateinit var buttonToSendCommentaryView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_comentarios)

        routerInterface = APIUtil.`interface`
        auth = FirebaseAuth.getInstance()

        commentsQuantityView = total_comments_view
        inputCommentaryView = et_commentary
        buttonToSendCommentaryView = send_button

        arrow_button.setOnClickListener{ finish() }

        //RESGATANDO IDPOST DE EXTRAS DA ACTIVITY
        val idPost = intent.getIntExtra("idPost", 0)

        findAndListComments(idPost)
        buttonToSendCommentaryView.setOnClickListener {
            sendCommentary(idPost)
        }
    }

    private fun sendCommentary(idPost: Int){
        val textContent = inputCommentaryView.text.toString()
        if (textContent.isEmpty()){
            return Toast.makeText(this, "Por favor, preencha o campo de comentário", Toast.LENGTH_SHORT).show()
        }

        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                val call: Call<MySqlResult> = routerInterface.commentPost(idToken, idPost, DataToCreateCommentary(textContent))
                call.enqueue(object : Callback<MySqlResult> {
                    override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                        if (response.isSuccessful) {
                            inputCommentaryView.text = convertStringtoEditable("")
                            commentsQuantityView.text = commentsQuantityView.text.toString().toInt().plus(1).toString()

                            findAndListComments(idPost)
                            Toast.makeText(this@ComentariosActivity, "Comentário enviado com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            Toast.makeText(
                                context, "Um erro ocorreu, tente novamente mais tarde.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                        Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NA LISTAGEM DE COMENTÁRIOS: $t")
                    }
                })
            }
    }

    private fun findAndListComments(idPost: Int){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!
                println("DEBUGANDO IDTOKEN: $idToken")

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                val call: Call<List<Commentary>> = routerInterface.getPostComments(idToken, idPost)
                call.enqueue(object : Callback<List<Commentary>> {
                    override fun onResponse(call: Call<List<Commentary>>, response: Response<List<Commentary>>) {
                        if (response.isSuccessful) {
                            /** RECEBER OS DADOS DA API  */
                            commentaryList = (response.body() as MutableList<Commentary>?)!!

                            //DEFININDO ADAPTER CONFORME O OBJETO COMENTÁRIO, MAS COM SUA ORDEM INVERTIDA
                            commentsQuantityView.text = commentaryList.size.toString()

                            val adapter = CommentaryItemAdapter(commentaryList.reversed(), context)
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

                                commentsQuantityView.text = "0"

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