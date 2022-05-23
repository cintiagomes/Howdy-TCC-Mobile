package com.example.howdy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.howdy.R
import com.example.howdy.adapter.ActivitiesAdapter
import com.example.howdy.adapter.PostsAdapter
import com.example.howdy.model.ActivityTypes.OneActivityInPublicList
import com.example.howdy.model.ActivityTypes.UnlockedAndCompletedActivities
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.convertStringtoEditable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_aprendizado.*
import kotlinx.android.synthetic.main.fragment_atividades.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AprendizadoFragment(idUser: Int) : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    var idUser = idUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aprendizado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //LISTANDO AS POSTAGENS PELA PRIMEIRA VEZ COM AS CONFIGURAÇÕES PADRÃO (POSTAGENS POPULARES)
        findAndListActivities()
    }

    private fun findAndListActivities(){
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!
                println("DEBUGANDO IDTOKEN: $idToken")

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                routerInterface = APIUtil.`interface`

                val call: Call<UnlockedAndCompletedActivities> = routerInterface.getActivitiesUnlockedAndCompletedByUser(
                    idToken,
                    idUser
                )
                call.enqueue(object : Callback<UnlockedAndCompletedActivities> {
                    override fun onResponse(call: Call<UnlockedAndCompletedActivities>, response: Response<UnlockedAndCompletedActivities>) {
                        if (response.isSuccessful) {
                            /** RECEBER OS DADOS DA API  */
                            var activitiesList: UnlockedAndCompletedActivities = response.body()!!
                            val unlockedActivitiesAdapter = ActivitiesAdapter(activitiesList.unlockedActivities, activity!!)
                            val completedActivitiesAdapter = ActivitiesAdapter(activitiesList.completedActivities, activity!!)

                            val unlockedActivitiesRecycler = recycler_atividades_desbloqueadas
                            val completedActivitiesRecycler = recycler_atividades_concluidas

                            unlockedActivitiesRecycler?.adapter = unlockedActivitiesAdapter
                            completedActivitiesRecycler?.adapter = completedActivitiesAdapter
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()

                            val unlockedActivitiesAdapter = ActivitiesAdapter(emptyList(), activity!!)
                            val completedActivitiesAdapter = ActivitiesAdapter(emptyList(), activity!!)

                            val unlockedActivitiesRecycler = recycler_atividades_desbloqueadas
                            val completedActivitiesRecycler = recycler_atividades_concluidas

                            unlockedActivitiesRecycler?.adapter = unlockedActivitiesAdapter
                            completedActivitiesRecycler?.adapter = completedActivitiesAdapter
                        }
                    }

                    override fun onFailure(call: Call<UnlockedAndCompletedActivities>, t: Throwable) {
                        Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()

                        val unlockedActivitiesAdapter = ActivitiesAdapter(emptyList(), activity!!)
                        val completedActivitiesAdapter = ActivitiesAdapter(emptyList(), activity!!)

                        val unlockedActivitiesRecycler = recycler_atividades_desbloqueadas
                        val completedActivitiesRecycler = recycler_atividades_concluidas

                        unlockedActivitiesRecycler?.adapter = unlockedActivitiesAdapter
                        completedActivitiesRecycler?.adapter = completedActivitiesAdapter

                        val adapter = PostsAdapter(emptyList(), activity!!)
                        val recycler = recycler_atividades
                        recycler?.adapter = adapter

                        println("DEBUGANDO - ONFAILURE NA LISTAGEM DE ATIVIDADES: $t")
                    }
                })

            }
    }
}