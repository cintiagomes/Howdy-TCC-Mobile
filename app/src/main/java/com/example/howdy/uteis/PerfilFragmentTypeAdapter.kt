package com.example.howdy.uteis

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.howdy.view.PostagensFragment

class PerfilFragmentTypeAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 1
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                PostagensFragment()
            }
//            1 -> {
//
//            }
//            2 -> {
//
//            }
//            3 -> {
//
//            }
            else -> {
                Fragment()
            }
        }
    }
}