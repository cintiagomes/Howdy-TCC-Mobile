package com.example.howdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.example.howdy.adapter.ChatItemAdapter
import com.example.howdy.adapter.CommentaryItemAdapter
import com.example.howdy.model.MessagesTypes.Message
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.network.SocketInstance
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.view.PerfilActivity
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.arrow_button
import kotlinx.android.synthetic.main.activity_chat.send_button
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private lateinit var routerInterface: RouterInterface
    private var auth = FirebaseAuth.getInstance()
    private lateinit var messageList: MutableList<Message>
    private lateinit var idToken: String

    private var mSocket: Socket? = null

    private var context = this

    private lateinit var userFriendProfilePhotoView: CircleImageView
    private lateinit var userFriendUserName: TextView
    private lateinit var editTextMessageView: EditText
    private lateinit var buttonToSendMessage: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_chat)

        routerInterface = APIUtil.`interface`
        auth = FirebaseAuth.getInstance()

        userFriendProfilePhotoView = profile_image
        userFriendUserName = name_text
        editTextMessageView = et_message
        buttonToSendMessage = send_button

        arrow_button.setOnClickListener { finish() }

        //RESGATANDO INFORMAÇÕES DO AMIGO, QUE ESTÁ EM EXTRAS
        val idUserFriend = intent.getIntExtra("idUser", 0)
        val friendUserName = intent.getStringExtra("userName")
        val friendProfilePhoto = intent.getStringExtra("profilePhoto")

        userFriendUserName.text = friendUserName

        if (friendProfilePhoto != null) {
            Glide
                .with(userFriendProfilePhotoView)
                .load(friendProfilePhoto)
                .into(userFriendProfilePhotoView)
        }

        findAndListComments(idUserFriend)

        putMessageListeners(idUserFriend)
        buttonToSendMessage.setOnClickListener {
            sendMessage(idUserFriend)
        }

        //IR PARA A PÁGINA DO USUÁRIO, QUANDO CLICAMOS EM SUA IMAGEM, OU NOME
        userFriendProfilePhotoView.setOnClickListener { goToUserActivity(idUserFriend) }
        userFriendUserName.setOnClickListener { goToUserActivity(idUserFriend) }
    }

    private fun putMessageListeners(idUserFriend: Int){
        //Socket instance
        val app: SocketInstance = application as SocketInstance
        mSocket = app.getMSocket()
        //connecting socket
        mSocket?.connect()
        val options = IO.Options()
        options.reconnection = true //reconnection
        options.forceNew = true //force new connection

        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!
                println("DEBUGANDO ANTES DE SE AUTENTICAR" + mSocket)
                try {
                    mSocket!!.io()!!.emit("authenticate", idToken)
                } catch (e: Exception) {
                    println("DEBUGANDO DEPOIS DE SE AUTENTICAR" + mSocket)
                }
                mSocket!!.io().on("receivedMessage") { args ->
                    println("DEBUGANDO CHEGOU ALGO: $args")
                    if (args[0] != null)
                    {
                        val data = args[0] as String
                        println("DEBUGANDO DATA: $data")
                        runOnUiThread {
                            println("DEBUGANDO DATA DENTRO: $data")
                        }
                    }
                }
            }
    }

    private fun sendMessage(idUserFriend: Int){
        val textContent = editTextMessageView.text.toString()
        if (textContent.isEmpty()){
            return Toast.makeText(this, "Por favor, preencha o campo antes de enviar.", Toast.LENGTH_SHORT).show()
        }

//        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
//        auth.currentUser?.getIdToken(true)
//            ?.addOnSuccessListener { result ->
//                idToken = result.token!!
//
//                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DAS MENSAGENS ANTERIORES
//                val call: Call<MySqlResult> = routerInterface.getPreviousMessages(idToken, idUserFriend, DataToCreateCommentary(textContent))
//                call.enqueue(object : Callback<MySqlResult> {
//                    override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
//                        if (response.isSuccessful) {
//                            inputCommentaryView.text = convertStringtoEditable("")
//                            commentsQuantityView.text = commentsQuantityView.text.toString().toInt().plus(1).toString()
//
//                            findAndListComments(idUserFriend)
//                            Toast.makeText(this@ComentariosActivity, "Comentário enviado com sucesso!", Toast.LENGTH_SHORT).show()
//                        } else {
//                            val jObjError = JSONObject(response.errorBody()!!.string())
//                            val errorMessage = jObjError.get("error").toString()
//                            Toast.makeText(
//                                context, "Um erro ocorreu, tente novamente mais tarde.",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
//                        Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
//                            Toast.LENGTH_LONG).show()
//                        println("DEBUGANDO - ONFAILURE NA LISTAGEM DE COMENTÁRIOS: $t")
//                    }
//                })
//            }
    }

    private fun findAndListComments(idUserFriend: Int){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                val call: Call<List<Message>> = routerInterface.getPreviousMessages(idToken, idUserFriend)
                call.enqueue(object : Callback<List<Message>> {
                    override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                        if (response.isSuccessful) {
                            /** RECEBER OS DADOS DA API  */
                            messageList = (response.body() as MutableList<Message>?)!!

                            val adapter = ChatItemAdapter(messageList.reversed(), context, idUserFriend)
                            val recycler = recycler_view_messages
                            recycler?.adapter = adapter
                        } else {
                            Toast.makeText(
                                context, "Um erro ocorreu, tente novamente mais tarde.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                        Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NA LISTAGEM DE MENSAGENS: $t")
                    }
                })
            }
    }

    private fun goToUserActivity(idUser:Int) {
        val targetPage = Intent(this, PerfilActivity::class.java)

        targetPage.putExtra("idUser", idUser);
        this.startActivity(targetPage)
    }
}