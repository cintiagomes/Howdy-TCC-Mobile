package com.example.howdy.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.howdy.R
import com.example.howdy.uteis.FragmentTypeAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

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
}

