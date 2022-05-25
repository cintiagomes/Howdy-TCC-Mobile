package com.example.howdy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.example.howdy.adapter.ActivitiesAdapter
import com.example.howdy.adapter.PeopleSearchItemAdapter
import com.example.howdy.model.ActivityTypes.OneActivityInPublicList
import com.example.howdy.model.UserTypes.UserInCollectedByName
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_pesquisa.*
import kotlinx.android.synthetic.main.activity_pesquisa.arrow_button
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PesquisaActivity : AppCompatActivity() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private val context = this

    private lateinit var nameTextView: TextView
    private lateinit var inputNameView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_pesquisa)

        arrow_button.setOnClickListener{ finish() }

        routerInterface = APIUtil.`interface`
        auth = FirebaseAuth.getInstance()

        nameTextView = text_name
        inputNameView = input_name

        //COLOCANDO OUVINTE NO INPUT DE FILTRO
        inputNameView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            //REALIZANDO A PESQUISA DE RANKING NOVAMENTE, CONSIDERANDO O NOVO FILTRO
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    nameTextView.text = s.toString()
                    findAndListUsers(s.toString())
                    findAndListActivities(s.toString())
                } else {
                    Toast.makeText(this@PesquisaActivity, "Digite um nome", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun findAndListUsers(name: String){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                routerInterface = APIUtil.`interface`

                val call: Call<List<UserInCollectedByName>> = routerInterface.getUsersByName(idToken, name)
                call.enqueue(object : Callback<List<UserInCollectedByName>> {
                    override fun onResponse(call: Call<List<UserInCollectedByName>>, response: Response<List<UserInCollectedByName>>) {
                        if (response.isSuccessful) {
                            /** RECEBER OS DADOS DA API  */
                            var userList: List<UserInCollectedByName> = response.body()!!
                            val adapter = PeopleSearchItemAdapter(userList, context)
                            val recycler = recycler_people
                            recycler?.adapter = adapter
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            if (errorMessage == "User not found") {
                                Toast.makeText(
                                    context, "Ops! Nenhum usuário foi encontrado.",
                                    Toast.LENGTH_LONG
                                ).show()

                                val adapter = PeopleSearchItemAdapter(emptyList(), context)
                                val recycler = recycler_people
                                recycler?.adapter = adapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<UserInCollectedByName>>, t: Throwable) {
                        Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NO RANKING: $t")
                    }
                })
            }
    }

    private fun findAndListActivities(name: String){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                routerInterface = APIUtil.`interface`

                val call: Call<List<OneActivityInPublicList>> = routerInterface.getActivitiesByName(idToken, name)
                call.enqueue(object : Callback<List<OneActivityInPublicList>> {
                    override fun onResponse(call: Call<List<OneActivityInPublicList>>, response: Response<List<OneActivityInPublicList>>) {
                        if (response.isSuccessful) {
                            println("DEBUGANDO - ONRESPONSE NO GET ACTIVITY: ${response.body()}")
                            /** RECEBER OS DADOS DA API  */
                            var activitiesList: List<OneActivityInPublicList> = response.body()!!
                            val adapter = ActivitiesAdapter(activitiesList, context)
                            val recycler = recycler_activities
                            recycler?.adapter = adapter
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            println("DEBUGANDO - ELSE: ${errorMessage}")
                            if (errorMessage == "No activities found") {
                                Toast.makeText(
                                    context, "Ops! Nenhuma atividade foi encontrada.",
                                    Toast.LENGTH_LONG
                                ).show()

                                val adapter = ActivitiesAdapter(emptyList(), context)
                                val recycler = recycler_activities
                                recycler?.adapter = adapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<OneActivityInPublicList>>, t: Throwable) {
                        println("DEBUGANDO - ONFAILURE: ${t.toString()}")
                        Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NO RANKING: $t")
                    }
                })
            }
    }
}