package com.example.chatapp.models

import android.util.Log
import com.example.chatapp.R
import com.example.chatapp.messages.LatestMessages
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessage(val chatMessage: ChatMessage): Item<GroupieViewHolder>(){

    var toUser: User? = User("", "", "")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartnerId: String

        if (chatMessage.fromId == LatestMessages.thisUser.uid) {
            chatPartnerId = chatMessage.toId
        } else chatPartnerId = chatMessage.fromId

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                toUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "User found: ${toUser?.username}")
                viewHolder.itemView.username_latest_message_row.text = toUser?.username
                viewHolder.itemView.latestmessage_latest_message_row.text = chatMessage.text
                Picasso.get().load(toUser?.profileImageUrl)
                    .into(viewHolder.itemView.profilepicture_latest_messages_row)


            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

}