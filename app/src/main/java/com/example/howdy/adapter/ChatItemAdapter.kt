package com.example.howdy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.howdy.R
import com.example.howdy.model.MessagesTypes.Message
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.model.TraductionTypes.DataToTranslate
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.view.PerfilActivity
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatItemAdapter(private val comments: List<Message>, private val activity: FragmentActivity, idUserFriend: Int) : RecyclerView.Adapter<ChatItemAdapter.Holder>() {
    var idUserFriend = idUserFriend

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = if (viewType == 0){
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user_logged, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_friend, parent, false)
        }
        return ChatViewHolder(itemView, activity)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemViewType(position: Int): Int {
        if (comments[position].idUserReceiver == idUserFriend) {
            return 0
        }
        return 1
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(obj: Message)
    }

    class ChatViewHolder(itemView: View, private val activity: FragmentActivity) : Holder(itemView) {
        private val auth = FirebaseAuth.getInstance()
        private val routerInterface: RouterInterface = APIUtil.`interface`

        private val textContentView: TextView = itemView.findViewById(R.id.text_content_view)
        private val traductionButtonView: ImageView = itemView.findViewById(R.id.btn_traduct)

        override fun bind(obj: Message) {
            textContentView.text = obj.textContent

            //TRADUZIR TEXTO QUANDO CLICADO
            traductionButtonView.setOnClickListener { handleTraduct(obj) }
        }

        private fun handleTraduct(message: Message){
            //VERIFICANDO SE O TEXTO QUE ESTÁ ESCRITO JÁ É UMA TRADUÇÃO
            if (textContentView.text == message.textContent){
                //VERIFICANDO SE ESTE POST JÁ POSSUI UMA TRADUÇÃO
                if (message.translatedTextContent == null) {
                    translateText(message)
                } else {
                    textContentView.text = message.translatedTextContent
                }
            } else {
                textContentView.text = message.textContent
            }
        }

        private fun translateText(message: Message){
            //RESGATANDO IDIOMA NATIVO DO USUÁRIO LOGADO
            val userLoggedFile = activity.getSharedPreferences(
                "userLogged", Context.MODE_PRIVATE)

            val nativeLanguageTranslatorName = userLoggedFile.getString("nativeLanguageTranslatorName", "")

            //RESGATANDO IDTOKEN DO USUÁRIO LOGADO
            auth.currentUser?.getIdToken(true)
                ?.addOnSuccessListener { result ->
                    val idToken = result.token

                    if (idToken != null && nativeLanguageTranslatorName != null) {
                        //EFETIVAR REQUISIÇÃO DE TRADUÇÃO
                        val dataToTranslate = DataToTranslate(
                            nativeLanguageTranslatorName,
                            arrayListOf(message.textContent)
                        )

                        val call: Call<List<String>> = routerInterface.traductText(idToken, dataToTranslate)
                        call.enqueue(object : Callback<List<String>> {
                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                if (response.isSuccessful) {
                                    message.translatedTextContent = response.body()?.get(0).toString()
                                    textContentView.text = message.translatedTextContent
                                } else {
                                    Toast.makeText(
                                        activity, "Ocorreu um erro na tradução.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                                    Toast.LENGTH_LONG).show()
                                println("DEBUGANDO - ONFAILURE NA TRADUÇÃO: $t")
                            }
                        })
                    }
                }
        }
    }
}