package com.example.howdy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.howdy.RankingFragment

class RankingFragmentTypeAdapter (fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    val fragmentActivity = fragmentActivity
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                RankingFragment("weekly")
            }
            1 -> {
                RankingFragment("monthly")
            }
            2 -> {
                RankingFragment("total")
            }
            else -> {
                Fragment()
            }
        }
    }
}