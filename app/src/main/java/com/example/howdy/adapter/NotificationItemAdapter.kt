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
import com.example.howdy.model.NotificationTypes.Notification
import com.example.howdy.utils.convertBackEndDateTimeFormatToSocialMediaFormat
import com.example.howdy.utils.convertStringtoEditable
import com.example.howdy.view.PerfilActivity
import de.hdodenhof.circleimageview.CircleImageView

class NotificationItemAdapter(private val notifications: List<Notification>, private val activity: FragmentActivity) : RecyclerView.Adapter<NotificationItemAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacao, parent, false)

        return UserViewHolder(itemView, activity)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        abstract fun bind(obj: Notification)
    }

    class UserViewHolder(itemView: View, private val activity: FragmentActivity) : Holder(itemView) {
        private val profilePhotoView: CircleImageView = itemView.findViewById(R.id.civ_user_profile_photo)
        private val userNameView: TextView = itemView.findViewById(R.id.tv_user_name)
        private val createdAtView: TextView = itemView.findViewById(R.id.tv_created_at)
        private val textContentView: TextView = itemView.findViewById(R.id.tv_notification_text)
        private val iconView: ImageView = itemView.findViewById(R.id.iv_notification_icon)

        override fun bind(obj: Notification) {
            userNameView.text = obj.userSenderName
            createdAtView.text = convertBackEndDateTimeFormatToSocialMediaFormat(obj.createdAt)
            textContentView.text = obj.notificationText

            //DEIXANDO O TEXTO EM NEGRITO, SE O USU??RIO N??O LEU ALGUMA NOTIFICA????O
            if(obj.wasRead == 0){
                //DEIXANDO O TEXTO BOLD
                textContentView.setTextColor(ContextCompat.getColor(activity, R.color.black))
            }

            //BUSCANDO A IMAGEM DO USU??RIO ATRAV??S DA URL, E INSERINDO NA RESPECTIVA IMAGE VIEW
            if(obj.userSenderProfilePhoto != null) {
                Glide
                    .with(profilePhotoView)
                    .load(obj.userSenderProfilePhoto)
                    .into(profilePhotoView)
            }

//            A DEPENDER DO TIPO DE NOTIFICA????O, O ??CONE SER?? UM DRAWBLE ALTERADO
            when(obj.type){
                "Commentary" -> {
                    iconView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_comentario_24))
                }
                "Friend request pending" ->{
                    iconView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_amizade_48))
                }
                "Friend Request Accepted" ->{
                    iconView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_amizade_aceita_48))
                }
                "Someone texted you" -> {
                    iconView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_mensagens_48))
                }
                "Someone bought your activity" -> {
                    iconView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_compra_atividade_48))
                }
                "Someone did your activity" -> {
                    iconView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icons_atividade_feita_48))
                }
            }


            //IR PARA A P??GINA DO USU??RIO, OU ABRIR O CHAT DEPENDENDO SE A VARI??VEL openChatOnClick FOR TRUE OU FALSE
            userNameView.setOnClickListener { goToUserActivity(obj.idUserSender) }
            profilePhotoView.setOnClickListener { goToUserActivity(obj.idUserSender) }
        }

        private fun goToUserActivity(idUser:Int) {
            val targetPage = Intent(activity, PerfilActivity::class.java)

            targetPage.putExtra("idUser", idUser);
            activity.startActivity(targetPage)
        }
    }
}