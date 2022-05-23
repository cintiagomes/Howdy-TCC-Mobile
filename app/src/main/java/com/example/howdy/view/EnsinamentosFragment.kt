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
import com.example.howdy.model.ActivityTypes.OneActivityCreatedBySomeone
import com.example.howdy.model.ActivityTypes.OneActivityInPublicList
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.model.UserTypes.Commenter
import com.example.howdy.model.UserTypes.UserCollectedWithId
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.convertStringtoEditable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_aprendizado.*
import kotlinx.android.synthetic.main.fragment_atividades.*
import kotlinx.android.synthetic.main.fragment_ensinamentos.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnsinamentosFragment(user: UserCollectedWithId) : Fragment() {
    private lateinit var routerInterface: RouterInterface
    private lateinit var auth: FirebaseAuth

    var user = user

    private lateinit var titlePage: TextView
    private lateinit var selectPriceView: AutoCompleteTextView
    private lateinit var selectDifficulty: AutoCompleteTextView
    private lateinit var bestRatingsButton: ImageButton
    private lateinit var newsButton: ImageButton

    private var currentMaxHowdyCoins: Int = 150
    private var currentIdDifficulty: Int = 2
    private var currentOrderBy: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_atividades, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titlePage = tv_title_page
        selectPriceView = select_preco
        selectDifficulty = select_dificuldade
        bestRatingsButton = ibtn_favorite
        newsButton = ibtn_news

        titlePage.text = "Atividades criadas por ${user.userName}"

        //DEFININDO VALORES PADRÕES
        selectPriceView.text = convertStringtoEditable("150 Howdy Coins")
        selectDifficulty.text = convertStringtoEditable("Intermediário")

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

                val call: Call<List<OneActivityCreatedBySomeone>> = routerInterface.getUserActivities(
                    idToken,
                    user.idUser,
                    currentMaxHowdyCoins,
                    currentIdDifficulty,
                    currentOrderBy
                )
                call.enqueue(object : Callback<List<OneActivityCreatedBySomeone>> {
                    override fun onResponse(call: Call<List<OneActivityCreatedBySomeone>>, response: Response<List<OneActivityCreatedBySomeone>>) {
                        if (response.isSuccessful) {
                            println("DEBUGANDO ON SUCESS")
                            /** RECEBER OS DADOS DA API  */
                            var activitiesWithoutCreatorData: List<OneActivityCreatedBySomeone> = response.body()!!
                            var activityWithCreatorList: MutableList<OneActivityInPublicList> = emptyList<OneActivityInPublicList>().toMutableList()

                            val userCreator = Commenter(
                                user.idUser,
                                user.userName,
                                user.profilePhoto,
                                user.isPro,
                                user.totalXp,
                                user.patent
                            )

                            for (i in activitiesWithoutCreatorData.indices) {
                                val activityFormatted = OneActivityInPublicList(
                                    userCreator,
                                    activitiesWithoutCreatorData[i].didIUnlockThisActivity,
                                    activitiesWithoutCreatorData[i].idActivity,
                                    activitiesWithoutCreatorData[i].activityCoverPhoto,
                                    activitiesWithoutCreatorData[i].activitySubtitle,
                                    activitiesWithoutCreatorData[i].activityTitle,
                                    activitiesWithoutCreatorData[i].description,
                                    activitiesWithoutCreatorData[i].createdAt,
                                    activitiesWithoutCreatorData[i].priceHowdyCoin,
                                    activitiesWithoutCreatorData[i].idTargetLanguage,
                                    activitiesWithoutCreatorData[i].targetLanguageName,
                                    activitiesWithoutCreatorData[i].totalRating,
                                    activitiesWithoutCreatorData[i].totalStars,
                                    activitiesWithoutCreatorData[i].starsRating,
                                    activitiesWithoutCreatorData[i].totalQuestion,
                                    activitiesWithoutCreatorData[i].totalTheoricalContentBlock,
                                    activitiesWithoutCreatorData[i].totalStudent
                                )

                                activityWithCreatorList.add(activityFormatted)
                            }

                            val adapter = ActivitiesAdapter(activityWithCreatorList, activity!!)
                            val recycler = recycler_atividades
                            recycler?.adapter = adapter
                        } else {
                            println("DEBUGANDO ELSE")
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.get("error").toString()
                            if (errorMessage == "This user has no activities.") {
                                Toast.makeText(
                                    activity, "Nenhuma atividade foi encontrada",
                                    Toast.LENGTH_LONG
                                ).show()

                                val adapter = ActivitiesAdapter(emptyList(), activity!!)
                                val recycler = recycler_atividades
                                recycler?.adapter = adapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<OneActivityCreatedBySomeone>>, t: Throwable) {
                        Toast.makeText(activity,"Houve um erro de conexão, verifique se está conectado na internet.",
                            Toast.LENGTH_LONG).show()

                        val adapter = ActivitiesAdapter(emptyList(), activity!!)
                        val recycler = recycler_atividades
                        recycler?.adapter = adapter

                        println("DEBUGANDO - ONFAILURE NA LISTAGEM DE ATIVIDADES: $t")
                    }
                })

            }
    }
}