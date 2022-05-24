package com.example.howdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.adapter.ChatItemAdapter
import com.example.howdy.model.MessagesTypes.DataToSendMessage
import com.example.howdy.model.MessagesTypes.Message
import com.example.howdy.network.Socketio
import io.socket.client.Socket
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.isUserPro
import com.example.howdy.view.PerfilActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.arrow_button
import kotlinx.android.synthetic.main.activity_chat.send_button
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var routerInterface: RouterInterface
    private var auth = FirebaseAuth.getInstance()
    private var messageList: MutableList<Message> = emptyList<Message>().toMutableList()
    private lateinit var idToken: String
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ChatItemAdapter

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

        //Socket instance
        Socketio.setSocket()
        mSocket = Socketio.getSocket()

        //connecting socket
        mSocket!!.connect()

        putMessageListeners(idUserFriend)
        buttonToSendMessage.setOnClickListener {
            sendMessage(idUserFriend)
        }

        //IR PARA A PÁGINA DO USUÁRIO, QUANDO CLICAMOS EM SUA IMAGEM, OU NOME
        userFriendProfilePhotoView.setOnClickListener { goToUserActivity(idUserFriend) }
        userFriendUserName.setOnClickListener { goToUserActivity(idUserFriend) }
    }

    private fun putMessageListeners(idUserFriend: Int){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!
                try {
                    mSocket!!.emit("authenticate", idToken)
                } catch (e: Exception) {
                }
                mSocket!!.on("receivedMessage") { args ->
                    val jsonMessage = args?.get(0)

                    //CONVERTENDO JSON PARA UM OBJETO MESSAGE UTILIZANDO GSON
                    val message = Gson().fromJson(jsonMessage.toString(), Message::class.java)

                    //RENDERIZANDO A MENSAGEM, SE ELA CORRESPONDER AO USUÁRIO QUE ESTAMOS CONVERSANDO
                    if (message.idUserSender == idUserFriend || message.idUserReceiver == idUserFriend) {
                        //INVERTENDO A LISTA DE MENSAGENS, ACRESCENTANDO A NOVA MENSAGEM, E INVERTENDO NOVAMENTE
                        val invertedList = messageList.reversed().toMutableList()
                        invertedList.add(message)
                        messageList = invertedList.reversed().toMutableList()

                        //RETORNANDO AO THREAD MAIN
                        runOnUiThread {
                            adapter = ChatItemAdapter(messageList.reversed(), context, idUserFriend)
                            recycler = recycler_view_messages
                            recycler?.adapter = adapter
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

        //RESGATANDO DE SHAREDPREFERENCES
        val sharedPreferences = getSharedPreferences("userLogged", 0)
        val subscriptionEndDate = sharedPreferences.getString("subscriptionEndDate", null)

        if (!isUserPro(Date(subscriptionEndDate))) return Toast.makeText(this, "Você precisa ser PRO para mandar mensagens", Toast.LENGTH_SHORT).show()

//        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!

                //CRIANDO UM OBJETO MESSAGE
                val dataToSendMessage = DataToSendMessage(
                    idToken,
                    idUserFriend,
                    textContent
                )

                //ENVIANDO A MENSAGEM PELO SOCKET
                mSocket!!.emit("sendMessage", Gson().toJson(dataToSendMessage))
                findAndListComments(idUserFriend)
            }
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

                            adapter = ChatItemAdapter(messageList.reversed(), context, idUserFriend)
                            recycler = recycler_view_messages
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