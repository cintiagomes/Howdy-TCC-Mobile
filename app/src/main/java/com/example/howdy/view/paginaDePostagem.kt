package com.example.howdy.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.howdy.R
import com.example.howdy.utils.FragmentTypeAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.SharedPreferences




class paginaDePostagem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagina_de_postagem)

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val viewpager = findViewById<ViewPager2>(R.id.viewpager)

        val adapter = FragmentTypeAdapter(this)
        viewpager.adapter = adapter

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_perfil -> {
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                perfil()
            }
            R.id.menu_ajuda -> {
                Toast.makeText(this, "Ajuda", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_ranking -> {
                Toast.makeText(this, "Ranking", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_assinatura -> {
                Toast.makeText(this, "Assinaturas", Toast.LENGTH_SHORT).show()
                assinatura()
            }
            R.id.menu_configuracoes -> {
                Toast.makeText(this, "Configurações", Toast.LENGTH_SHORT).show()
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

    private fun assinatura() {
        val assinar = Intent(this, InicioAssinatura::class.java)
        startActivity(assinar)
    }

    private fun perfil() {
        val targetPage = Intent(this, PerfilActivity::class.java)
        startActivity(targetPage)
    }

    private fun paginaMain() {
        val targetPage = Intent(this, MainActivity::class.java)
        startActivity(targetPage)
    }

}

