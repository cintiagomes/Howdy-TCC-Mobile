package com.example.howdy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner

class AtividadesFragment : Fragment() {

    val planets_array = arrayOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//       val adapter = ArrayAdapter.createFromResource(this, android.R.layout.simple_spinner_item, planets_array)

//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_atividades, container, false)

    }


}