package com.example.howdy.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.howdy.adapter.RankingItemAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_amigos.*
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AmigosFragment(idUser: Int?) : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private lateinit var totalFriendsView: TextView

    var idUser = idUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_amigos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        totalFriendsView = tv_total_friends

        //LISTANDO OS AMIGOS DO USUÁRIO
        findAndListFriends()
    }

    private fun findAndListFriends() {
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!

                try {
                    //RESGATANDO O ID DO USUÁRIO LOGADO, CASO NÃO TENHA NENHUM IDUSER
                    if (idUser == null) {
                        val userLoggedFile = requireActivity().getSharedPreferences(
                            "userLogged", Context.MODE_PRIVATE)

                        idUser = userLoggedFile.getInt("idUser", 0)
                    }

                    //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                    routerInterface = APIUtil.`interface`

                    val call: Call<List<Friend>> = routerInterface.getAllSomeoneFriends(idToken, idUser!!
                    )
                    call.enqueue(object : Callback<List<Friend>> {
                        override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                            if (response.isSuccessful) {
                                try {
                                    /** RECEBER OS DADOS DA API  */
                                    var friendsList: List<Friend> = response.body()!!

                                    //MARCANDO NA VIEW QUANTOS AMIGOS FORAM ENCONTRADOS
                                    totalFriendsView.text =
                                        friendsList.size.toString() +
                                                " amigo" +
                                                if(friendsList.size > 1) "s" else ""

                                    val adapter = FriendItemAdapter(friendsList, activity!!, false)
                                    val recycler = recycler_amigos
                                    recycler?.adapter = adapter
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                try {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    val errorMessage = jObjError.get("error").toString()
                                    if (errorMessage == "This user has no friends ;-;") {
                                        Toast.makeText(
                                            activity, "Ops! Nenhum amigo foi encontrado.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        val adapter = FriendItemAdapter(emptyList(), activity!!, false)
                                        val recycler = recycler_amigos
                                        recycler?.adapter = adapter
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                            Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                                Toast.LENGTH_LONG).show()
                            println("DEBUGANDO - ONFAILURE NA LISTAGEM DE AMIGOS: $t")
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

}