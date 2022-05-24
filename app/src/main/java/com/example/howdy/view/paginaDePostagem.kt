package com.example.howdy.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.howdy.*
import com.example.howdy.adapter.FragmentTypeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pagina_de_postagem.*
import kotlinx.android.synthetic.main.toolbar.*


class paginaDePostagem : AppCompatActivity() {

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_pagina_de_postagem)

        container_menu.setOnClickListener {
            onAddButtonClicked()
        }

        menu_perfil.setOnClickListener {
            Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()

            //RESGATANDO ID DO USUÁRIO LOGADO
            val userLoggedFile = this.getSharedPreferences(
                "userLogged", Context.MODE_PRIVATE)

            val idUser = userLoggedFile.getInt("idUser", 0)

            perfil(idUser)
        }

        menu_ranking.setOnClickListener {
            Toast.makeText(this, "Ranking", Toast.LENGTH_SHORT).show()
            ranking()
        }

        menu_assinatura.setOnClickListener {
            Toast.makeText(this, "Assinaturas", Toast.LENGTH_SHORT).show()
            assinatura()
        }

        menu_configuracoes.setOnClickListener {
            Toast.makeText(this, "Configurações", Toast.LENGTH_SHORT).show()
            configuracoes()
        }

        menu_sair.setOnClickListener {
            Toast.makeText(this, "Saindo da conta", Toast.LENGTH_SHORT).show()

            val prefsEditor = getSharedPreferences("userLogged", 0).edit()
            prefsEditor.clear()
            prefsEditor.commit()

            Firebase.auth.signOut()

            paginaMain()
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val viewpager = findViewById<ViewPager2>(R.id.viewpager)

        val adapter = FragmentTypeAdapter(this)
        viewpager.adapter = adapter

        search_button.setOnClickListener {
            val pesquisar = Intent(this, PesquisaActivity::class.java)
            startActivity(pesquisar)
        }

        coins_howdy.setOnClickListener {
            val assinaturas = Intent(this, InicioAssinatura::class.java)
            startActivity(assinaturas)
        }

        TabLayoutMediator(tabLayout, viewpager){ tab, pos ->
            when(pos){
                0 -> {tab.setIcon(R.drawable.ic_home_24) }
                1 -> {tab.setIcon(R.drawable.ic_people_24)}
                2 -> {tab.setIcon(R.drawable.ic_book_24)}
                3 -> {tab.setIcon(R.drawable.ic_notifications_24)}
                4 -> {tab.setIcon(R.drawable.ic_chat_24)}
            }
        }.attach()

    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked){
            menu_perfil.visibility = View.VISIBLE
            menu_ranking.visibility = View.VISIBLE
            menu_assinatura.visibility = View.VISIBLE
            menu_configuracoes.visibility = View.VISIBLE
            menu_sair.visibility = View.VISIBLE
        }else{
            menu_perfil.visibility = View.INVISIBLE
            menu_ranking.visibility = View.INVISIBLE
            menu_assinatura.visibility = View.INVISIBLE
            menu_configuracoes.visibility = View.INVISIBLE
            menu_sair.visibility = View.INVISIBLE
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked){
            menu_perfil.startAnimation(fromBottom)
            menu_ranking.startAnimation(fromBottom)
            menu_assinatura.startAnimation(fromBottom)
            menu_configuracoes.startAnimation(fromBottom)
            menu_sair.startAnimation(fromBottom)
            container_menu.startAnimation(rotateOpen)
        }else{
            menu_perfil.startAnimation(toBottom)
            menu_ranking.startAnimation(toBottom)
            menu_assinatura.startAnimation(toBottom)
            menu_configuracoes.startAnimation(toBottom)
            menu_sair.startAnimation(toBottom)
            container_menu.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean){
        if (!clicked){
            menu_perfil.isClickable = true
            menu_ranking.isClickable = true
            menu_assinatura.isClickable = true
            menu_configuracoes.isClickable = true
            menu_sair.isClickable = true
        }else{
            menu_perfil.isClickable = false
            menu_ranking.isClickable = false
            menu_assinatura.isClickable = false
            menu_configuracoes.isClickable = false
            menu_sair.isClickable = false
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_perfil -> {
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()

                //RESGATANDO ID DO USUÁRIO LOGADO
                val userLoggedFile = this.getSharedPreferences(
                    "userLogged", Context.MODE_PRIVATE)

                val idUser = userLoggedFile.getInt("idUser", 0)

                perfil(idUser)
            }
            R.id.menu_ajuda -> {
                Toast.makeText(this, "Ajuda", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_ranking -> {
                Toast.makeText(this, "Ranking", Toast.LENGTH_SHORT).show()
                ranking()
            }
            R.id.menu_assinatura -> {
                Toast.makeText(this, "Assinaturas", Toast.LENGTH_SHORT).show()
                assinatura()
            }
            R.id.menu_configuracoes -> {
                Toast.makeText(this, "Configurações", Toast.LENGTH_SHORT).show()
                configuracoes()
            }
            R.id.menu_sair -> {
                Toast.makeText(this, "Saindo da conta", Toast.LENGTH_SHORT).show()

                val prefsEditor = getSharedPreferences("userLogged", 0).edit()
                prefsEditor.clear()
                prefsEditor.commit()

                Firebase.auth.signOut()

                paginaMain()
            }
        }

        return true
    }

    private fun configuracoes() {
        val configuracoes = Intent(this, ConfiguracaoActivity::class.java)
        startActivity(configuracoes)
    }

    private fun ranking() {
        val ranking = Intent(this, RankingActivity::class.java)
        startActivity(ranking)
    }

    private fun assinatura() {
        val assinar = Intent(this, InicioAssinatura::class.java)
        startActivity(assinar)
    }

    private fun perfil(idUser:Int) {
        val targetPage = Intent(this, PerfilActivity::class.java)
        targetPage.putExtra("idUser", idUser)
        startActivity(targetPage)
    }

    private fun paginaMain() {
        val targetPage = Intent(this, MainActivity::class.java)
        startActivity(targetPage)
    }

}

