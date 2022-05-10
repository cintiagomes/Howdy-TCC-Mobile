package com.example.howdy.utils.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.howdy.model.UserTypes.UserCollectedWithId
import com.example.howdy.view.AmigosPerfilFragment
import com.example.howdy.view.AprendizadoFragment
import com.example.howdy.view.EnsinamentosFragment
import com.example.howdy.view.PostagensFragment

class PerfilFragmentTypeAdapter(fragmentActivity: FragmentActivity, user: UserCollectedWithId): FragmentStateAdapter(fragmentActivity) {
    val user = user
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                PostagensFragment(user)
            }
            1 -> {
                AmigosPerfilFragment()
            }
            2 -> {
                AprendizadoFragment()
            }
            3 -> {
                EnsinamentosFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}