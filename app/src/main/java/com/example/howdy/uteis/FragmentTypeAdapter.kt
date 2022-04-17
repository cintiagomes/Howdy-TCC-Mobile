package com.example.howdy.uteis

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.howdy.AtividadesFragment
import com.example.howdy.view.ChatFragment
import com.example.howdy.NotificacaoFragment
import com.example.howdy.view.AmigosFragment
import com.example.howdy.view.HomeFragment

class FragmentTypeAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                HomeFragment()
            }
            1 -> {
                AmigosFragment()
            }
            2 -> {
                AtividadesFragment()
            }
            3 -> {
                NotificacaoFragment()
            }
            4 -> {
                ChatFragment()
            }
            else -> {
                Fragment()
            }
        }
    }

}