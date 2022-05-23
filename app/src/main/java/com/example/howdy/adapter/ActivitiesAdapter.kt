package com.example.howdy.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howdy.R
import com.example.howdy.model.ActivityTypes.OneActivityInPublicList
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.example.howdy.view.PerfilActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.howdy.utils.convertBackEndDateTimeFormatToSocialMediaFormat

class ActivitiesAdapter(private val activities: List<OneActivityInPublicList>, private val activity: FragmentActivity) : RecyclerView.Adapter<ActivitiesAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_atividade, parent, false)
        return HomeViewHolder(itemView, activity)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(activities[position])
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(obj: OneActivityInPublicList)
    }

    class HomeViewHolder(itemView: View, private val activity: FragmentActivity) : Holder(itemView) {
        private val auth = FirebaseAuth.getInstance()
        private val routerInterface: RouterInterface = APIUtil.`interface`

        private val userCreatorNameView: TextView = itemView.findViewById(R.id.user_creator_name_view)
        private val userCreatorPatentView: ImageView = itemView.findViewById(R.id.iv_user_creator_patent)
        private val userCreatorProfilePhotoView: ImageView = itemView.findViewById(R.id.user_creator_profile_photo_view)
        private val createdAtView: TextView = itemView.findViewById(R.id.created_at_view)
        private val targetLanguageView: TextView = itemView.findViewById(R.id.tv_target_language_name)
        private val titleView: TextView = itemView.findViewById(R.id.tv_title_activity)
        private val descriptionView: TextView = itemView.findViewById(R.id.tv_description_activity)
        private val priceHowdyCoinsView: TextView = itemView.findViewById(R.id.tv_price_howdy_coins)
        private val btnAcessActivity: Button = itemView.findViewById(R.id.btn_acess_activity)

        override fun bind(obj: OneActivityInPublicList) {
            userCreatorNameView.text = obj.userCreator.userName
            userCreatorNameView.text = obj.userCreator.userName

            //BUSCANDO A IMAGEM DO USUÁRIO ATRAVÉS DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
            if(obj.userCreator.profilePhoto != null) {
                Glide
                    .with(userCreatorProfilePhotoView)
                    .load(obj.userCreator.profilePhoto)
                    .into(userCreatorProfilePhotoView)
            }
            //EXIBINDO PATENTE DO CRIADOR DA POSTAGEM, SE ELE FOR PRO
            if(obj.userCreator.patent != null){
                userCreatorPatentView.visibility = View.VISIBLE
                putPatentImage(obj.userCreator.patent!!)
            }

            targetLanguageView.text = obj.targetLanguageName
            titleView.text = obj.activityTitle + " - " + obj.activitySubtitle
            descriptionView.text = obj.description
            priceHowdyCoinsView.text = if(obj.priceHowdyCoin == 0) "Grátis" else obj.priceHowdyCoin.toString()

            //FORMATANDO DATA PARA INSERIR NA TELA
            val formattedCreatedAt = convertBackEndDateTimeFormatToSocialMediaFormat(obj.createdAt)
            createdAtView.text = formattedCreatedAt

            //IR PARA A PÁGINA DO CRIADOR DA POSTAGEM, QUANDO CLICAMOS EM SUA IMAGEM, OU NOME
            userCreatorProfilePhotoView.setOnClickListener { goToUserCreatorActivity(obj.userCreator.idUser) }
            userCreatorNameView.setOnClickListener { goToUserCreatorActivity(obj.userCreator.idUser) }

            btnAcessActivity.setOnClickListener { goToActivity(obj.idActivity) }
        }

        private fun goToActivity(idActivity: Int){
            Toast.makeText(activity, "Acessando a atividade $idActivity", Toast.LENGTH_SHORT).show()
//            val intent = Intent(activity, ActivityActivity::class.java)
//            intent.putExtra("idActivity", idActivity)
//            activity.startActivity(intent)
        }

        private fun putPatentImage(patent: String){
            when (patent) {
                "noob" -> {
                    userCreatorPatentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_noob
                        )
                    )
                }
                "beginner" -> {
                    userCreatorPatentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_beginner
                        )
                    )
                }
                "amateur" -> {
                    userCreatorPatentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_amateur
                        )
                    )
                }
                "experient" -> {
                    userCreatorPatentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_experient
                        )
                    )
                }
                "veteran" -> {
                    userCreatorPatentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_veteran
                        )
                    )
                }
                else -> {
                    userCreatorPatentView.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.classe_master
                        )
                    )
                }
            }
        }

        private fun goToUserCreatorActivity(idUser:Int) {
            val targetPage = Intent(activity, PerfilActivity::class.java)

            targetPage.putExtra("idUser", idUser);
            activity.startActivity(targetPage)
        }
    }
}