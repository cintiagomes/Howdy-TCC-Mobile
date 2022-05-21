package com.example.howdy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.howdy.adapter.ActivitiesAdapter
import com.example.howdy.adapter.PostsAdapter
import com.example.howdy.model.ActivityTypes.OneActivityInPublicList
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.convertStringtoEditable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_atividades.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context as Context1

class AtividadesFragment : Fragment() {

    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    private lateinit var titlePage: TextView
    private lateinit var selectPriceView: AutoCompleteTextView
    private lateinit var selectDifficulty: AutoCompleteTextView
    private lateinit var bestRatingsButton: ImageButton
    private lateinit var newsButton: ImageButton

    private var currentMaxHowdyCoins: Int = 150
    private var currentIdDifficulty: Int = 2
    private var currentOrderBy: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titlePage = tv_title_page
        selectPriceView = select_preco
        selectDifficulty = select_dificuldade
        bestRatingsButton = ibtn_favorite
        newsButton = ibtn_news

        titlePage.text = "Atividades públicas"

        val prices = resources.getStringArray(R.array.howdy_coin_max_price_activity)
        val adapterPrices = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_item,
            prices
        )

        with(selectPriceView){
            setAdapter(adapterPrices)
        }

        val difficulties = resources.getStringArray(R.array.activity_difficulties)
        val adapterDifficulties = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_item,
            difficulties
        )

        with(selectDifficulty){
            setAdapter(adapterDifficulties)
        }

        //DEFININDO VALORES PADRÕES
        selectPriceView.text = convertStringtoEditable("150 Howdy Coins")
        selectDifficulty.text = convertStringtoEditable("Intermediário")

        //LISTANDO AS POSTAGENS PELA PRIMEIRA VEZ COM AS CONFIGURAÇÕES PADRÃO (POSTAGENS POPULARES)
        findAndListActivities("rating")

        //COLOCANDO UM OUVINTE EM CADA BOTÃO, PARA QUE A CATEGORIA DAS POSTAGENS SEJA ALTERADA
        putEventListenerInFilters()
    }

    private fun putEventListenerInFilters(){
        bestRatingsButton.setOnClickListener { view ->
            run {
                unselectAllButtonsOrder()

                bestRatingsButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_popular_background)

                findAndListActivities("rating")
            }
        }

        newsButton.setOnClickListener { view ->
            run {
                unselectAllButtonsOrder()

                newsButton.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.selected_sports_background)

                findAndListActivities("recents")
            }
        }

        //COLOCANDO UM OUVINTE EM CADA SELECT, QUANDO SEU VALOR É ALTERADO
        selectPriceView.setOnItemClickListener { parent, _, position, _ ->
            run {
                val newSelectedPrice = selectPriceView.text.toString()
                currentMaxHowdyCoins = when(newSelectedPrice){
                    "10 Howdy Coins" -> 10
                    "50 Howdy Coins" -> 50
                    "100 Howdy Coins" -> 100
                    "150 Howdy Coins" -> 150
                    else -> 0
                }

                findAndListActivities(null)
            }
        }

        selectDifficulty.setOnItemClickListener { parent, _, position, _ ->
            run {
                val newSelectedDifficulty = selectDifficulty.text.toString()
                currentIdDifficulty = when(newSelectedDifficulty){
                    "Básico" -> 1
                    "Intermediário" -> 2
                    else -> 3
                }

                findAndListActivities(null)
            }
        }
    }

    private fun unselectAllButtonsOrder(){
        bestRatingsButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_popular_background)

        newsButton.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.unselected_sports_background)
    }

    private fun findAndListActivities(orderBySelected: String?){
        if (orderBySelected != null){
            if (orderBySelected == currentOrderBy)
                return Toast.makeText(
                    activity,"Você já está ordenando atividades desta forma.",
                    Toast.LENGTH_LONG
                ).show()

            currentOrderBy = orderBySelected
        }

        //RESGATANDO IDTOKEN ATUAL DO USUÁRIO
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!
                println("DEBUGANDO IDTOKEN: $idToken")

                //O USUÁRIO ESTÁ LOGADO, E FARÁ A LISTAGEM DE POSTAGENS
                routerInterface = APIUtil.`interface`

                val call: Call<List<OneActivityInPublicList>> = routerInterface.getPublicActivities(
                    idToken,
                    currentMaxHowdyCoins,
                    currentIdDifficulty,
                    currentOrderBy
                )
                call.enqueue(object : Callback<List<OneActivityInPublicList>> {
                    override fun onResponse(call: Call<List<OneActivityInPublicList>>, response: Response<List<OneActivityInPublicList>>) {
                        if (response.isSuccessful) {
                            /** RECEBER OS DADOS DA API  */
                            var activitiesList: List<OneActivityInPublicList> = response.body()!!
                            val adapter = ActivitiesAdapter(activitiesList, activity!!)
                            val recycler = recycler_atividades
                            recycler?.adapter = adapter
                        } else {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            if (errorMessage == "No activities found") {
                                Toast.makeText(
                                    activity, "Nenhuma atividade foi encontrada",
                                    Toast.LENGTH_LONG
                                ).show()

                                val adapter = PostsAdapter(emptyList(), activity!!)
                                val recycler = recycler_user_posts
                                recycler?.adapter = adapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<OneActivityInPublicList>>, t: Throwable) {
                        Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()
                        println("DEBUGANDO - ONFAILURE NA LISTAGEM DE ATIVIDADES: $t")
                    }
                })

            }
    }
}