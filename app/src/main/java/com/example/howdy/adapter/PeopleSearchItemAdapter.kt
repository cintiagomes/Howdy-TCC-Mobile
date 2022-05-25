package com.example.howdy.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.UserTypes.UserInCollectedByName
import com.example.howdy.view.PerfilActivity
import de.hdodenhof.circleimageview.CircleImageView

class PeopleSearchItemAdapter(private val users: List<UserInCollectedByName>, private val activity: FragmentActivity) : RecyclerView.Adapter<PeopleSearchItemAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pesquisa_pessoas, parent, false)

        return UserViewHolder(itemView, activity)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(obj: UserInCollectedByName)
    }

    class UserViewHolder(itemView: View, private val activity: FragmentActivity) : Holder(itemView) {
        private val totalXpView: TextView = itemView.findViewById(R.id.tv_total_xp)
        private val profilePhotoView: CircleImageView = itemView.findViewById(R.id.civ_user_profile_photo)
        private val userNameView: TextView = itemView.findViewById(R.id.tv_user_name)
        private val targetLanguageNameView: TextView = itemView.findViewById(R.id.tv_target_language_name)
        private val patentView: ImageView = itemView.findViewById(R.id.iv_patent)

        override fun bind(obj: UserInCollectedByName) {
            totalXpView.text = obj.totalXp.toString() + " XP"
            targetLanguageNameView.text = obj.targetLanguageName
            userNameView.text = obj.userName

            //BUSCANDO A IMAGEM DO USUÁRIO ATRAVÉS DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
            if(obj.profilePhoto != null) {
                Glide
                    .with(profilePhotoView)
                    .load(obj.profilePhoto)
                    .into(profilePhotoView)
            }

            //EXIBINDO PATENTE DO USUÁRIO, SE ELE FOR PRO
            if(obj.patent != null){
                putPatentImage(obj.patent!!)
            }


            //IR PARA A PÁGINA DO USUÁRIO, OU ABRIR O CHAT DEPENDENDO SE A VARIÁVEL openChatOnClick FOR TRUE OU FALSE

            userNameView.setOnClickListener { goToUserActivity(obj.idUser) }
            profilePhotoView.setOnClickListener { goToUserActivity(obj.idUser) }
        }

        private fun putPatentImage(patent: String){
            when (patent) {
                "noob" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_noob
                        )
                    )
                }
                "beginner" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_beginner
                        )
                    )
                }
                "amateur" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_amateur
                        )
                    )
                }
                "experient" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_experient
                        )
                    )
                }
                "veteran" -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_veteran
                        )
                    )
                }
                else -> {
                    patentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_master
                        )
                    )
                }
            }
        }

        private fun goToUserActivity(idUser:Int) {
            val targetPage = Intent(activity, PerfilActivity::class.java)

            targetPage.putExtra("idUser", idUser);
            activity.startActivity(targetPage)
        }
    }
}