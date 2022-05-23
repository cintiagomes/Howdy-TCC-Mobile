package com.example.howdy.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.R
import com.example.howdy.adapter.FriendItemAdapter
import com.example.howdy.model.UserTypes.Friend
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_amigos.*
import kotlinx.android.synthetic.main.fragment_chat.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment() : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //LISTANDO OS AMIGOS DO USUÁRIO
        findAndListFriends()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)

    }

    private fun findAndListFriends() {
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!

                //RESGATANDO O ID DO USUÁRIO LOGADO

                val userLoggedFile = requireActivity().getSharedPreferences(
                    "userLogged", Context.MODE_PRIVATE)

                val idUser = userLoggedFile.getInt("idUser", 0)

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                routerInterface = APIUtil.`interface`

                val call: Call<List<Friend>> = routerInterface.getAllSomeoneFriends(idToken, idUser!!
                )
                call.enqueue(object : Callback<List<Friend>> {
                    override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                        if (response.isSuccessful) {
                            /** RECEBER OS DADOS DA API  */
                            var friendsList: List<Friend> = response.body()!!

                            val adapter = FriendItemAdapter(friendsList, activity!!, true)
                            val recycler = chat_recycler_view
                            recycler?.adapter = adapter
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            println("DEBUGANDO ERRO" + errorMessage)
                            if (errorMessage == "This user has no friends ;-;") {
                                Toast.makeText(
                                    activity, "Ops! Nenhum amigo foi encontrado.",
                                    Toast.LENGTH_LONG
                                ).show()

                                val adapter = FriendItemAdapter(emptyList(), activity!!, true)
                                val recycler = recycler_amigos
                                recycler?.adapter = adapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                        Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NA LISTAGEM DE AMIGOS: $t")
                    }
                })
            }
    }
}

data class Contact(val nome: String, val mensagem: String) {

}
