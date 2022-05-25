package com.example.howdy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.howdy.adapter.NotificationItemAdapter
import com.example.howdy.adapter.PeopleSearchItemAdapter
import com.example.howdy.model.NotificationTypes.Notification
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_amigos.*
import kotlinx.android.synthetic.main.fragment_notificacao.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificacaoFragment : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private lateinit var titlePageView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notificacao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        routerInterface = APIUtil.`interface`
        auth = FirebaseAuth.getInstance()

        titlePageView = text_view_titulo
        findAndListNotifications()
    }

    private fun findAndListNotifications() {
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                routerInterface = APIUtil.`interface`

                if (activity != null) {
                    val call: Call<List<Notification>> = routerInterface.getNotifications(idToken)
                    call.enqueue(object : Callback<List<Notification>> {
                        override fun onResponse(
                            call: Call<List<Notification>>,
                            response: Response<List<Notification>>
                        ) {
                            if (response.isSuccessful) {
                                println("DEBUGANDO ONSUCESS")
                                /** RECEBER OS DADOS DA API  */
                                //DESCOBRINDO QUANTAS NOTIFICAÇÕES POSSUI O WAS READ COMO FALSE
                                var numberOfNotReadNotifications = 0
                                for (notification in response.body()!!) {
                                    if (notification.wasRead == 0) {
                                        numberOfNotReadNotifications++
                                    }
                                }


                                titlePageView.text =
                                    "Notificações - " + numberOfNotReadNotifications + " não lidas"

                                var notificationList: List<Notification> = response.body()!!
                                val adapter = NotificationItemAdapter(notificationList, activity!!)
                                val recycler = recycler_notificacao
                                recycler?.adapter = adapter

                                routerInterface.readNotifications(idToken)
                            } else {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jObjError.get("error").toString()

                                println("DEBUGANDO ELSE" + errorMessage)
                                if (errorMessage == "User not found") {
                                    Toast.makeText(
                                        activity, "Ops! Nenhum usuário foi encontrado.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val adapter = PeopleSearchItemAdapter(emptyList(), activity!!)
                                    val recycler = recycler_notificacao
                                    recycler?.adapter = adapter
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                            println("FAILURE")
                            Toast.makeText(
                                activity,
                                "Houve um erro de conexão, verifique se está conectado na internet.",
                                Toast.LENGTH_LONG
                            ).show()
                            println("DEBUGANDO - ONFAILURE NA LISTAGEM DE NOTIFICAÇÕES: $t")
                        }
                    })
                }
            }
    }
}