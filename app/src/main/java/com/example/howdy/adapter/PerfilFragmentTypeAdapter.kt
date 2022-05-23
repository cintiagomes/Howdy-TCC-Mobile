package com.example.howdy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.howdy.model.UserTypes.UserCollectedWithId
import com.example.howdy.view.*

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
                AmigosFragment(user.idUser)
            }
            2 -> {
                AprendizadoFragment(user.idUser)
            }
            3 -> {
                EnsinamentosFragment(user)
            }
            else -> {
                Fragment()
            }
        }
    }
}