package com.example.howdy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.model.UserTypes.UserInRanking
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.adapter.PostsAdapter
import com.example.howdy.utils.adapter.RankingItemAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingFragment(rankingType: String, fragmentActivity:FragmentActivity) : Fragment() {
    private val rankingType = rankingType
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private lateinit var rankingTitleView: TextView
    private lateinit var nameFilterField: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rankingTitleView = tv_ranking_title
        nameFilterField = et_name_filter_ranking

        val initialTitleText = "Ranking de ganho de XP"
        val rankingTypeInPortuguese: String

        //DEFININDO O TÍTULO DA VIEW
        if(rankingType == "weekly") rankingTypeInPortuguese = "semanal"
        else if(rankingType == "monthly") rankingTypeInPortuguese = "mensal"
        else rankingTypeInPortuguese = "total"

        rankingTitleView.text = "$initialTitleText $rankingTypeInPortuguese."

        //COLOCANDO OUVINTE NO INPUT DE FILTRO
        nameFilterField.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            //REALIZANDO A PESQUISA DE RANKING NOVAMENTE, CONSIDERANDO O NOVO FILTRO
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                findAndListRanking(s.toString())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //LISTANDO AS POSTAGENS PELA PRIMEIRA VEZ COM AS CONFIGURAÇÕES PADRÃO (POSTAGENS POPULARES)
        findAndListRanking("")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    private fun findAndListRanking(nameFilter: String) {
        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!
                println("DEBUGANDO IDTOKEN: $idToken")

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                routerInterface = APIUtil.`interface`

                val call: Call<List<UserInRanking>> = routerInterface.getXpRanking(idToken, rankingType, nameFilter)
                call.enqueue(object : Callback<List<UserInRanking>> {
                    override fun onResponse(call: Call<List<UserInRanking>>, response: Response<List<UserInRanking>>) {
                        if (response.isSuccessful) {
                            /** RECEBER OS DADOS DA API  */
                            var userList: List<UserInRanking> = response.body()!!
                            val adapter = RankingItemAdapter(userList, activity!!)
                            val recycler = recycler_ranking
                            recycler?.adapter = adapter
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            if (errorMessage == "No users found in this ranking") {
                                Toast.makeText(
                                    activity, "Ops! Nenhum foi encontrado.",
                                    Toast.LENGTH_LONG
                                ).show()

                                val adapter = RankingItemAdapter(emptyList(), activity!!)
                                val recycler = recycler_ranking
                                recycler?.adapter = adapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<UserInRanking>>, t: Throwable) {
                        Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NO RANKING: $t")
                    }
                })
            }
    }

}