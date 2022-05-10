package com.example.howdy.utils.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.UserTypes.UserInRanking
import com.example.howdy.view.PerfilActivity
import de.hdodenhof.circleimageview.CircleImageView

class RankingItemAdapter(private val users: List<UserInRanking>, private val activity: FragmentActivity) : RecyclerView.Adapter<RankingItemAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ranking, parent, false)

        return RankingViewHolder(itemView, activity)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(obj: UserInRanking)
    }

    class RankingViewHolder(itemView: View, private val activity: FragmentActivity) : Holder(itemView) {
        private val positionRankingView: TextView = itemView.findViewById(R.id.tv_position_ranking)
        private val xpCollectedView: TextView = itemView.findViewById(R.id.tv_xp_collected)
        private val profilePhotoView: CircleImageView = itemView.findViewById(R.id.civ_profile_photo)
        private val userNameView: TextView = itemView.findViewById(R.id.user_name)
        private val targetLanguageNameView: TextView = itemView.findViewById(R.id.tv_target_language_name)

        override fun bind(obj: UserInRanking) {
            positionRankingView.text = obj.positionRanking.toString() + "º"
            xpCollectedView.text = obj.totalXp.toString() + " XP"
            targetLanguageNameView.text = obj.targetLanguageName
            userNameView.text = obj.userName

            //VERIFICANDO SE DEVO PINTAR A COR DESTA POSIÇÃO, NO CAS DE INSERIR COR OURO, PRATA, OU BRONZE
            if (obj.positionRanking <= 3) changePositionColor(obj.positionRanking, activity)

            //BUSCANDO A IMAGEM DO USUÁRIO ATRAVÉS DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
            if(obj.profilePhoto != null) {
                Glide
                    .with(profilePhotoView)
                    .load(obj.profilePhoto)
                    .into(profilePhotoView)
            }


            //IR PARA A PÁGINA DO CRIADOR DA POSTAGEM, QUANDO CLICAMOS EM SUA IMAGEM, OU NOME
            userNameView.setOnClickListener { goToUserActivity(obj.idUser) }
            profilePhotoView.setOnClickListener { goToUserActivity(obj.idUser) }
        }

        private fun changePositionColor(position: Int, activity: FragmentActivity){
            val color = (
                    when (position) {
                        1 -> Color.parseColor("#D9FFCC4D")
                        2 -> Color.parseColor("#B6B6B6")
                        else -> Color.parseColor("#CD7F32")
                    })

            positionRankingView.setTextColor(color)
        }

        private fun goToUserActivity(idUser:Int) {
            val targetPage = Intent(activity, PerfilActivity::class.java)

            targetPage.putExtra("idUser", idUser);
            activity.startActivity(targetPage)
        }
    }
}