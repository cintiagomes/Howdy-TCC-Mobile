package com.example.howdy

import android.provider.ContactsContract
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contact: List<ContactsContract.Contacts>)
    : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contact[position])
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(data: ContactsContract.Contacts){
            with(itemView){
                val txtNome = findViewById<TextView>(R.id.textNome)
                val txtMensagem = findViewById<TextView>(R.id.textMensagem)
            }
        }

    }

}