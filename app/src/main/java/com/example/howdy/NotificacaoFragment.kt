package com.example.howdy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.howdy.adapter.ChatItemAdapter
import com.example.howdy.adapter.NotificationItemAdapter
import com.example.howdy.adapter.PeopleSearchItemAdapter
import com.example.howdy.model.MessagesTypes.Message
import com.example.howdy.model.MySqlResult
import com.example.howdy.model.NotificationTypes.Notification
import com.example.howdy.network.Socketio
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_amigos.*
import kotlinx.android.synthetic.main.fragment_notificacao.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificacaoFragment : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private var mSocket: Socket? = null
    private lateinit var idToken: String

    private var notificationList: MutableList<Notification> = emptyList<Notification>().toMutableList()
    private var numberOfNotReadNotifications: Int = 0

    private lateinit var titlePageView: TextView
    private lateinit var recycler: RecyclerView

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

        recycler = recycler_notificacao
        titlePageView = text_view_titulo
        findAndListNotifications()

        //Socket instance
        Socketio.setSocket()
        mSocket = Socketio.getSocket()

        //connecting socket
        mSocket!!.connect()

        putNotificationListeners()
    }

    private fun putNotificationListeners(){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!
                try {
                    mSocket!!.emit("authenticate", idToken)
                } catch (e: Exception) {
                }
                mSocket!!.on("receivedNotification") { args ->
                    val jsonNotification = args?.get(0)

                    //CONVERTENDO JSON PARA UM OBJETO MESSAGE UTILIZANDO GSON
                    val notification = Gson().fromJson(jsonNotification.toString(), Notification::class.java)

                    //INVERTENDO A LISTA DE NOTIFICAÇÕES, ACRESCENTANDO A NOVA NOTIFICAÇÃO, E INVERTENDO NOVAMENTE
                    val invertedList = notificationList.reversed().toMutableList()
                    invertedList.add(notification)
                    notificationList = invertedList.reversed().toMutableList()

                    //RETORNANDO AO THREAD MAIN
                    activity?.runOnUiThread {
                        //ATUALIZANDO NÚMERO DE NOTIFICAÇÕES NÃO LIDAS
                        numberOfNotReadNotifications++

                        titlePageView.text =
                            "Notificações - " + numberOfNotReadNotifications + " não lida" + if (numberOfNotReadNotifications != 1) "s" else ""

                        //ATUALIZANDO A LISTA DE NOTIFICAÇÕES
                        recycler.adapter = NotificationItemAdapter(notificationList, requireActivity())

                        readNotifications(idToken)
                    }
                }
            }
    }

    private fun findAndListNotifications() {
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                idToken = result.token!!

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
                                /** RECEBER OS DADOS DA API  */
                                //DESCOBRINDO QUANTAS NOTIFICAÇÕES POSSUI O WAS READ COMO FALSE
                                numberOfNotReadNotifications = 0
                                for (notification in response.body()!!) {
                                    if (notification.wasRead == 0) {
                                        numberOfNotReadNotifications++
                                    }
                                }


                                titlePageView.text =
                                    "Notificações - " + numberOfNotReadNotifications + " não lida" + if (numberOfNotReadNotifications != 1) "s" else ""

                                notificationList = response.body()!!.toMutableList()
                                val adapter = NotificationItemAdapter(notificationList, activity!!)
                                recycler?.adapter = adapter
                            } else {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                val errorMessage = jObjError.get("error").toString()

                                if (errorMessage == "No notifications found") {
                                    Toast.makeText(
                                        activity, "Você não possui nenhuma notificação para ler",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val adapter = PeopleSearchItemAdapter(emptyList(), activity!!)
                                    recycler?.adapter = adapter
                                }
                            }

                            readNotifications(idToken!!)
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

    private fun readNotifications(idToken: String){
        val call: Call<MySqlResult> = routerInterface.readNotifications(idToken)
        call.enqueue(
            object : Callback<MySqlResult> {
                override fun onResponse(
                    call: Call<MySqlResult>,
                    response: Response<MySqlResult>
                ) {}

                override fun onFailure(
                    call: Call<MySqlResult>,
                    t: Throwable
                ) {}
            }
        );
    }
}