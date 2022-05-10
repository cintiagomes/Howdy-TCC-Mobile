package com.example.howdy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.viewpager2.widget.ViewPager2
import com.example.howdy.utils.adapter.RankingFragmentTypeAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_ranking)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button_voltar)

        arrowButton.setOnClickListener{ finish() }

        val tabLayout = findViewById<TabLayout>(R.id.tabs_ranking)
        val viewpager2 = findViewById<ViewPager2>(R.id.viewpager3)

        val adapter = RankingFragmentTypeAdapter(this)
        viewpager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewpager2){ tab, pos ->
            when(pos){
                0 -> {tab.text="Semanal"}
                1 -> {tab.text="Mensal"}
                2 -> {tab.text="Total"}
            }
        }.attach()

    }
}