package com.example.howdy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.BaseObj
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.howdy.adapter.PostsAdapter
import com.example.howdy.model.PostTypes.Post
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.firebase.auth.FirebaseAuth
import convertBackEndDateTimeFormatToSocialMediaFormat
import kotlinx.android.synthetic.main.activity_comentarios.*
import java.util.ArrayList

class ComentariosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(R.layout.activity_comentarios)

        arrow_button.setOnClickListener{ finish() }

        val itens = arrayListOf(
            1 to "titulo"
        )

        val adapter = ComentaryAdpeter(itens)
        findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter

    }

    class ComentaryAdpeter(private val itens: ArrayList<Pair<Int, String>>) : RecyclerView.Adapter<ComentaryAdpeter.HolderComentar>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentaryHolderView {
            val  itemView = if (viewType == 0){
                    LayoutInflater.from(parent.context).inflate(R.layout.item_comentar, parent, false)
                }else {
                LayoutInflater.from(parent.context).inflate(R.layout.item_comentar, parent, false)
                }
            return ComentaryHolderView(itemView)
        }

        override fun getItemViewType(position: Int): Int {
            return itens[position].first
        }

        override fun onBindViewHolder(holder: HolderComentar, position: Int) {

        }

        abstract class HolderComentar (itemView: View) : RecyclerView.ViewHolder(itemView){
            abstract fun bind(obj: Commentary)

        }

        override fun getItemCount(): Int {
            return itens.size
        }

        class ComentaryHolderView (itemView: View) : HolderComentar(itemView){

            private val auth = FirebaseAuth.getInstance()
            private val routerInterface: RouterInterface = APIUtil.`interface`

            private val userCreatorNameComment: TextView = itemView.findViewById(R.id.user_creator_name_comment)
            private val userCreatorPhotoComment: ImageView = itemView.findViewById(R.id.user_creator_photo_comment)
            private val textComment: TextView = itemView.findViewById(R.id.text_comment)
            private val createdAtComment: TextView = itemView.findViewById(R.id.created_at_comment)


            override fun bind(obj: Commentary) {
                userCreatorNameComment.text = obj.commenter.toString()
                textComment.text = obj.textCommentary


            }

        }

    }

}