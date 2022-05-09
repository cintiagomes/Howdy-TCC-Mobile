package com.example.howdy.view

import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.UserTypes.UserCollectedWithId
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.utils.PerfilFragmentTypeAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.item_ranking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PerfilActivity : AppCompatActivity() {
    private val routerInterface: RouterInterface = APIUtil.`interface`
    private val auth = FirebaseAuth.getInstance()
    private val context = this

    private lateinit var user: UserCollectedWithId

    private lateinit var backgroundImageView: ImageView
    private lateinit var profilePhotoView: CircleImageView
    private lateinit var userNameView: TextView
    private lateinit var targetLanguageNameView: TextView
    private lateinit var nativeLanguageNameView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var totalXpView: TextView
    private lateinit var graphView: GraphView

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

        graphView = graph

        val series: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)
            )
        )
        graph.addSeries(series)

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
    }
}