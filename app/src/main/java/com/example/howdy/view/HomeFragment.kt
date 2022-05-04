package com.example.howdy.view

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.howdy.R
import com.example.howdy.model.MySqlResult
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RetroFitClient
import com.example.howdy.remote.RouterInterface
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call

class HomeFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val itens = arrayListOf(
            0 to "item A",
            0 to "item B",
            0 to "item C"
        )

        val adapter = HomeAdapter(itens)
        println("DEBUGANDO: $view")
        val recycler = recycler_postagens
        recycler?.adapter = adapter
    }

    class HomeAdapter(private val itens: ArrayList<Pair<Int, String>>) : RecyclerView.Adapter<HomeAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_postagens, parent, false)
            return HomeViewHolder(itemView)
        }

        override fun getItemViewType(position: Int): Int {
            return itens[position].first

        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(itens[position])
        }

        override fun getItemCount(): Int {
            return itens.size
        }

        abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
            abstract fun bind(obj: Pair<Int, String>)
        }

        class HomeViewHolder(itemView: View) : Holder(itemView) {
            private val nomeUser: TextView = itemView.findViewById(R.id.name_user)
            private val mensagemUser: TextView = itemView.findViewById(R.id.mensagem_user)
            private val fotoUser: CircleImageView = itemView.findViewById(R.id.foto_user)

            override fun bind(obj: Pair<Int, String>) {
                nomeUser.text = obj.second
                mensagemUser.text = obj.second
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

}