package com.example.howdy.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.howdy.RankingMensalFragment
import com.example.howdy.RankingSemanalFragment
import com.example.howdy.RankingTotalFragment

class RankingFragmentTypeAdapter (fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                RankingSemanalFragment()
            }
            1 -> {
                RankingMensalFragment()
            }
            2 -> {
                RankingTotalFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}