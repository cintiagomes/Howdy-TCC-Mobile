package com.example.howdy.view

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.UserTypes.UserCollectedWithId
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.adapter.PerfilFragmentTypeAdapter
import com.example.howdy.model.Friendship
import com.example.howdy.model.MySqlResult
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PerfilActivity : AppCompatActivity() {
    private val routerInterface: RouterInterface = APIUtil.`interface`
    private val auth = FirebaseAuth.getInstance()
    private val context = this

    private lateinit var user: UserCollectedWithId

    private lateinit var friendshipButton1: ImageView
    private lateinit var friendshipButton2: ImageView
    private lateinit var backgroundImageView: ImageView
    private lateinit var profilePhotoView: CircleImageView
    private lateinit var userNameView: TextView
    private lateinit var targetLanguageNameView: TextView
    private lateinit var nativeLanguageNameView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var totalXpView: TextView
    private lateinit var userPatentView: ImageView
    private lateinit var weeklyGraphView: GraphView
    private lateinit var monthlyGraphView: GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_perfil)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button)

        arrowButton.setOnClickListener{ finish() }

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val viewpager2 = findViewById<ViewPager2>(R.id.viewpager2)

        //RESGATANDO ID DO REPRESENTANTE DA PÁGINA, PARA INSERIR NA QUERY
        val idUser: Int = intent.extras?.getInt("idUser")!!

        backgroundImageView = iv_background_image
        profilePhotoView = civ_profile_photo
        userNameView = tv_user_name
        targetLanguageNameView = tv_target_language_name
        nativeLanguageNameView = tv_native_language_name
        descriptionView = tv_description
        totalXpView = tv_total_xp
        userPatentView = civ_user_patent

        weeklyGraphView = weekly_xp_chart
        monthlyGraphView = monthly_xp_chart
        friendshipButton1 = iv_friendship_btn_1
        friendshipButton2 = iv_friendship_btn_2

        getAndRenderUserData(idUser, tabLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun getAndRenderUserData(idUser: Int, tabLayout: TabLayout){
        /** RESGATANDO DADOS DO REPRESENTANTE DA ACTIVITY PELO ID **/
        //RESGATANDO IDTOKEN DO USUÁRIO LOGADO
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token
                if (idToken != null) {
                    val call: Call<List<UserCollectedWithId>> = routerInterface.getUserById(idToken, idUser)
                    call.enqueue(object : Callback<List<UserCollectedWithId>> {
                        override fun onResponse(call: Call<List<UserCollectedWithId>>, response: Response<List<UserCollectedWithId>>) {
                            if (response.isSuccessful) {
                                user = response.body()?.get(0)!!
                                checkIfUserIsMyFriendAndRenderButton(idToken)
                                renderUserData()

                                val adapter = PerfilFragmentTypeAdapter(context, user)
                                viewpager2.adapter = adapter

                                TabLayoutMediator(tabLayout, viewpager2){ tab, pos ->
                                    when(pos){
                                        0 -> {tab.text="Postagens"}
                                        1 -> {tab.text="Amigos"}
                                        2 -> {tab.text="Aprendizado"}
                                        3 -> {tab.text="Ensinamentos"}
                                    }
                                }.attach()
                            } else {
                                Toast.makeText(
                                    context, "OPS... OCORREU UM ERRO AO RESGATAR OS DADOS DO USUÁRIO!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<List<UserCollectedWithId>>, t: Throwable) {
                            Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                                Toast.LENGTH_LONG).show()
                            println("DEBUGANDO - ONFAILURE EM GET USER BY ID $t")
                        }
                    })
                }
            }
    }

    private fun checkIfUserIsMyFriendAndRenderButton(idToken: String){
        val call: Call<Friendship> = routerInterface.isUserMyFriend(idToken, user.idUser)
        call.enqueue(object : Callback<Friendship> {
            override fun onResponse(call: Call<Friendship>, response: Response<Friendship>) {
                if (response.isSuccessful) {
                    val friendship = response.body()
                    if (friendship?.message != null) {
                        renderFriendshipButton(idToken, null, friendship!!.message)
                    } else {
                        renderFriendshipButton(idToken, friendship)
                    }
                } else {
                    //RESGATANDO MENSAGEM DE ERRO
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.get("error")?.toString()

                    if (errorMessage != "This user is not your friend ;-;") {
                        Toast.makeText(
                            context, "OPS... OCORREU UM ERRO AO RESGATAR OS DADOS DO USUÁRIO!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        renderFriendshipButton(idToken, null, errorMessage)
                    }
                }
            }

            override fun onFailure(call: Call<Friendship>, t: Throwable) {
                Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                    Toast.LENGTH_LONG).show()
                println("DEBUGANDO - ONFAILURE EM GET USER FRIENDS $t")
            }
        })
    }

    private fun renderFriendshipButton(idToken: String, friendship: Friendship?, errorMessage: String? = null){
        //RESGATANDO ID DO USUÁRIO LOGADO, PARA VERIFICAR QUAL BOTÃO DE AMIZADE DEVERÁ SER RENDERIZADO
        val userLoggedFile = this.getSharedPreferences(
            "userLogged", Context.MODE_PRIVATE)

        val idUserLogged = userLoggedFile.getInt("idUser", 0)

        val buttonsState: String?

        println("DEBUGANDO - ID DO USUÁRIO LOGADO: $idUserLogged")
        println("DEBUGANDO - errorMessage: ${errorMessage}")
        println("DEBUGANDO - friendship : ${friendship.toString()} - ${friendship?.message} - ${friendship?.idFriendship} - ${friendship?.idUserAcceptor} - ${friendship?.idFriendship} - ${friendship?.idUserSender}")

        //DECIDINDO QUAL BOTÃO DE AMIZADE DEVERÁ SER RENDERIZADO
        if(errorMessage == "This user is not your friend ;-;") {
            //DEIXAR O BOTÃO 2 VISÍVEL, E 1 INVISÍVEL
            changeFriendshipButtonVisibility(View.INVISIBLE, View.VISIBLE)

            //ALTERANDO O SRC DA IMAGEM DO BOTÃO 2 PELO DRAWBLE ic_person_add_24, E SEU BACKGROUND POR unselected_sports_background
            friendshipButton2.setImageResource(R.drawable.ic_person_add_24)
            friendshipButton2.setBackgroundResource(R.drawable.unselected_sports_background)

            buttonsState = "sendRequest"
        } else if(friendship?.isPending === 0) {
            //OS USUÁRIOS SÃO AMIGOS, DEIXAR O BOTÃO 1 INVISÍVEL, E 2 VISÍVEL
            changeFriendshipButtonVisibility(View.INVISIBLE, View.VISIBLE)

            //ALTERANDO O SRC DA IMAGEM DO BOTÃO 2 PELO DRAWBLE ic_person_remove_24, E SEU BACKGROUND POR unselected_friends_background
            friendshipButton2.setImageResource(R.drawable.ic_person_remove_24)
            friendshipButton2.setBackgroundResource(R.drawable.unselected_friends_background)

            buttonsState = "cancelFriendship"
        } else if (friendship?.isPending == 1 && idUserLogged == friendship?.idUserSender) {
            /**O USUÁRIO LOGADO QUE MANDOU A SOLICITAÇÃO, ENTÃO ELE PODERÁ CANCELAR A SOLICITAÇÃO **/
            //DEIXAR O BOTÃO 2 VISÍVEL, E 1 INVISÍVEL
            changeFriendshipButtonVisibility(View.INVISIBLE, View.VISIBLE)

            //ALTERANDO O SRC DA IMAGEM DO BOTÃO 2 PELO DRAWBLE ic_person_add_disabled_24, E SEU BACKGROUND POR unselected_friends_background
            friendshipButton2.setImageResource(R.drawable.ic_person_add_disabled_24)
            friendshipButton2.setBackgroundResource(R.drawable.unselected_friends_background)

            buttonsState = "cancelRequest"
        }else if (friendship?.isPending == 1 && idUserLogged == friendship?.idUserAcceptor) {
            /**O USUÁRIO LOGADO QUE RECEBEU A SOLICITAÇÃO, ENTÃO ELE PODERÁ ACEITAR OU RECUSAR A SOLICITAÇÃO **/
            //DEIXAR O BOTÃO 2 VISÍVEL, E 1 VISÍVEL
            changeFriendshipButtonVisibility(View.VISIBLE, View.VISIBLE)

            //ALTERANDO O SRC DA IMAGEM DO BOTÃO 1 PELO DRAWBLE ic_close_24, E SEU BACKGROUND POR unselected_friends_background
            friendshipButton1.setImageResource(R.drawable.ic_close_24)
            friendshipButton1.setBackgroundResource(R.drawable.unselected_friends_background)

            //ALTERANDO O SRC DA IMAGEM DO BOTÃO 2 PELO DRAWBLE ic_check_24, E SEU BACKGROUND POR unselected_sports_background
            friendshipButton2.setImageResource(R.drawable.ic_check_24)
            friendshipButton2.setBackgroundResource(R.drawable.unselected_sports_background)

            buttonsState = "acceptOrDeclineRequest"
        } else {
            //DEIXAR O BOTÃO 1 INVISÍVEL, E 2 INVISÍVEIS
            changeFriendshipButtonVisibility(View.INVISIBLE, View.INVISIBLE)

            buttonsState = "desactivated"
        }

        setOnClickListenersInFriendshipButtons(buttonsState, idUserLogged, idToken)
    }

    private fun setOnClickListenersInFriendshipButtons(buttonsState: String, idUserLogged: Int, idToken: String){
        when(buttonsState){
            "sendRequest" -> {
                friendshipButton2.setOnClickListener {
                    sendFriendshipRequest(idUserLogged, idToken)
                }
            }
            "cancelRequest" -> {
                friendshipButton2.setOnClickListener {
                    deleteFriendship(idUserLogged, idToken)
                }
            }
            "cancelFriendship" -> {
                friendshipButton2.setOnClickListener {
                    deleteFriendship(idUserLogged, idToken)
                }
            }
            "acceptOrDeclineRequest" -> {
                friendshipButton1.setOnClickListener {
                    deleteFriendship(idUserLogged, idToken)
                }
                friendshipButton2.setOnClickListener {
                    acceptFriendshipRequest(idUserLogged, idToken)
                }
            }
        }
    }

    private fun acceptFriendshipRequest(idUserLogged: Int, idToken: String){
        val call: Call<MySqlResult> = routerInterface.acceptFriendshipRequest(idToken, user.idUser)
        call.enqueue(object : Callback<MySqlResult> {
            override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                if (response.isSuccessful) {
                    checkIfUserIsMyFriendAndRenderButton(idToken)
                } else {
                    Toast.makeText(
                        context, "OPS... OCORREU UM ERRO!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                    Toast.LENGTH_LONG).show()
                println("DEBUGANDO - ONFAILURE EM CLICK FRIEND BUTTON $t")
            }
        })
    }

    private fun deleteFriendship(idUserLogged: Int, idToken: String){
        val call: Call<MySqlResult> = routerInterface.deleteFriendship(idToken, user.idUser)
        call.enqueue(object : Callback<MySqlResult> {
            override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                if (response.isSuccessful) {
                    checkIfUserIsMyFriendAndRenderButton(idToken)
                } else {
                    Toast.makeText(
                        context, "OPS... OCORREU UM ERRO!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                    Toast.LENGTH_LONG).show()
                println("DEBUGANDO - ONFAILURE EM CLICK FRIEND BUTTON $t")
            }
        })
    }

    private fun sendFriendshipRequest(idUserLogged: Int, idToken: String){
        val call: Call<MySqlResult> = routerInterface.createFriendshipRequest(idToken, user.idUser)
        call.enqueue(object : Callback<MySqlResult> {
            override fun onResponse(call: Call<MySqlResult>, response: Response<MySqlResult>) {
                if (response.isSuccessful) {
                    checkIfUserIsMyFriendAndRenderButton(idToken)
                } else {
                    Toast.makeText(
                        context, "OPS... OCORREU UM ERRO!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<MySqlResult>, t: Throwable) {
                Toast.makeText(context,"Houve um erro de conexão, verifique se está conectado na internet.",
                    Toast.LENGTH_LONG).show()
                println("DEBUGANDO - ONFAILURE EM CLICK FRIEND BUTTON $t")
            }
        })
    }

    private fun changeFriendshipButtonVisibility(buttonOneVisibility: Int, buttonTwoVisibility: Int){
        friendshipButton1.visibility = buttonOneVisibility
        friendshipButton2.visibility = buttonTwoVisibility
    }

    private fun putPatentImage(patent: String){
        when (patent) {
            "noob" -> {
                userPatentView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.classe_noob
                    )
                )
            }
            "beginner" -> {
                userPatentView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.classe_beginner
                    )
                )
            }
            "amateur" -> {
                userPatentView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.classe_amateur
                    )
                )
            }
            "experient" -> {
                userPatentView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.classe_experient
                    )
                )
            }
            "veteran" -> {
                userPatentView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.classe_veteran
                    )
                )
            }
            else -> {
                userPatentView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.classe_master
                    )
                )
            }
        }
    }

    fun renderUserData(){
        userNameView.text = user.userName
        targetLanguageNameView.text = user.targetLanguageName
        nativeLanguageNameView.text = user.nativeLanguageName
        descriptionView.text = user.description
        totalXpView.text = user.totalXp.toString() + " XP"

        if(user.profilePhoto != null) {
            Glide
                .with(profilePhotoView)
                .load(user.profilePhoto)
                .into(profilePhotoView)
        }

        if(user.backgroundImage != null) {
            Glide
                .with(backgroundImageView)
                .load(user.backgroundImage)
                .into(backgroundImageView)
        }

        //EXIBINDO PATENTE DO CRIADOR DA POSTAGEM, SE ELE FOR PRO
        if(user.patent != null){
            putPatentImage(user.patent!!)
        }

        //ACRESCENTANDO HISTÓRICOS DE XP NOS GRÁFICOS

        val weeklyChartColumnSpacing = 9.0 / 7
        val weeklySeries: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(
                DataPoint(weeklyChartColumnSpacing * 0, user.xpCharts.weekly[0]),
                DataPoint(weeklyChartColumnSpacing * 1.0, user.xpCharts.weekly[1]),
                DataPoint(weeklyChartColumnSpacing * 2.0, user.xpCharts.weekly[2]),
                DataPoint(weeklyChartColumnSpacing * 3.0, user.xpCharts.weekly[3]),
                DataPoint(weeklyChartColumnSpacing * 4.0, user.xpCharts.weekly[4]),
                DataPoint(weeklyChartColumnSpacing * 5.0, user.xpCharts.weekly[5]),
                DataPoint(weeklyChartColumnSpacing * 6.0, user.xpCharts.weekly[6])
            )
        )

        val monthlySeriesChartColumnSpacing = 9.0 / 30
        val monthlySeries: LineGraphSeries<DataPoint> = LineGraphSeries(
                arrayOf(
                    DataPoint(monthlySeriesChartColumnSpacing * 0.0, user.xpCharts.monthly[0]),
                    DataPoint(monthlySeriesChartColumnSpacing * 1.0, user.xpCharts.monthly[1]),
                    DataPoint(monthlySeriesChartColumnSpacing * 2.0, user.xpCharts.monthly[2]),
                    DataPoint(monthlySeriesChartColumnSpacing * 3.0, user.xpCharts.monthly[3]),
                    DataPoint(monthlySeriesChartColumnSpacing * 4.0, user.xpCharts.monthly[4]),
                    DataPoint(monthlySeriesChartColumnSpacing * 5.0, user.xpCharts.monthly[5]),
                    DataPoint(monthlySeriesChartColumnSpacing * 6.0, user.xpCharts.monthly[6]),
                    DataPoint(monthlySeriesChartColumnSpacing * 7.0, user.xpCharts.monthly[7]),
                    DataPoint(monthlySeriesChartColumnSpacing * 8.0, user.xpCharts.monthly[8]),
                    DataPoint(monthlySeriesChartColumnSpacing * 9.0, user.xpCharts.monthly[9]),
                    DataPoint(monthlySeriesChartColumnSpacing * 10.0, user.xpCharts.monthly[10]),
                    DataPoint(monthlySeriesChartColumnSpacing * 11.0, user.xpCharts.monthly[11]),
                    DataPoint(monthlySeriesChartColumnSpacing * 12.0, user.xpCharts.monthly[12]),
                    DataPoint(monthlySeriesChartColumnSpacing * 13.0, user.xpCharts.monthly[13]),
                    DataPoint(monthlySeriesChartColumnSpacing * 14.0, user.xpCharts.monthly[14]),
                    DataPoint(monthlySeriesChartColumnSpacing * 15.0, user.xpCharts.monthly[15]),
                    DataPoint(monthlySeriesChartColumnSpacing * 16.0, user.xpCharts.monthly[16]),
                    DataPoint(monthlySeriesChartColumnSpacing * 17.0, user.xpCharts.monthly[17]),
                    DataPoint(monthlySeriesChartColumnSpacing * 18.0, user.xpCharts.monthly[18]),
                    DataPoint(monthlySeriesChartColumnSpacing * 19.0, user.xpCharts.monthly[19]),
                    DataPoint(monthlySeriesChartColumnSpacing * 20.0, user.xpCharts.monthly[20]),
                    DataPoint(monthlySeriesChartColumnSpacing * 21.0, user.xpCharts.monthly[21]),
                    DataPoint(monthlySeriesChartColumnSpacing * 22.0, user.xpCharts.monthly[22]),
                    DataPoint(monthlySeriesChartColumnSpacing * 23.0, user.xpCharts.monthly[23]),
                    DataPoint(monthlySeriesChartColumnSpacing * 24.0, user.xpCharts.monthly[24]),
                    DataPoint(monthlySeriesChartColumnSpacing * 25.0, user.xpCharts.monthly[25]),
                    DataPoint(monthlySeriesChartColumnSpacing * 26.0, user.xpCharts.monthly[26]),
                    DataPoint(monthlySeriesChartColumnSpacing * 27.0, user.xpCharts.monthly[27]),
                    DataPoint(monthlySeriesChartColumnSpacing * 28.0, user.xpCharts.monthly[28]),
                    DataPoint(monthlySeriesChartColumnSpacing * 29.0, user.xpCharts.monthly[29]),
            )
        )

        weeklyGraphView.addSeries(weeklySeries)
        monthlyGraphView.addSeries(monthlySeries)
    }
}