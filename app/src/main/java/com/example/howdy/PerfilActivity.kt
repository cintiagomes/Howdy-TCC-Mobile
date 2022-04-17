package com.example.howdy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.viewpager2.widget.ViewPager2
import com.example.howdy.uteis.FragmentTypeAdapter
import com.example.howdy.uteis.PerfilFragmentTypeAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val viewpager2 = findViewById<ViewPager2>(R.id.viewpager2)

        val adapter = PerfilFragmentTypeAdapter(this)
        viewpager2.adapter = adapter

//        TabLayoutMediator(tabLayout, viewpager2){ tab, pos ->
//            when(pos){
//                0 -> {tab.text="Postagens"
//                    tab
//                }
//                1 -> {tab.text="Postagens"}
//                2 -> {tab.text="Postagens"}
//                3 -> {tab.text="Postagens"}
//            }
//        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

}