package com.example.howdy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.howdy.R

class ChatFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)

    }

    private fun getContacts(): List<Contact>{
        return arrayListOf(
            Contact("Henry da Silva", "Oi como vc está?...")
        ).toList()
    }
}

data class Contact(val nome: String, val mensagem: String) {

}
