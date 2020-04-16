package com.example.chatapp.chatlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp.R
import com.example.chatapp.messages.LatestMessages
import com.example.chatapp.models.User
import com.example.chatapp.messages.NewMessageActivity
import com.example.chatapp.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.profilepicture_chat_to_row
import kotlinx.android.synthetic.main.chat_to_row.view.*

var toUser = User()
var fromUser = User()
val adapter = GroupAdapter<GroupieViewHolder>()

class ChatLogActivity : AppCompatActivity() {
    companion object{
        val TAG = "ChatLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        adapter.clear()
        recyclerview_chatLog.adapter = adapter
        fromUser = LatestMessages.thisUser
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)!!
        supportActionBar?.title = toUser.username

        sendBtn_chatLog.setOnClickListener {
            Log.d(TAG,"Send message clicked")
            SendMessage()
            textInput_edittext_chatLog.text.clear()
            recyclerview_chatLog.scrollToPosition(adapter.itemCount - 1)
        }

         listenForMessages()
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/${fromUser.uid}/${toUser.uid}")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG, "found message: " + chatMessage!!.text)

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    adapter.add(ChatToItem(chatMessage.text))
                }
                else {
                    adapter.add(ChatFromItem(chatMessage.text))
                }
                recyclerview_chatLog.scrollToPosition(adapter.itemCount - 1)
            }

            //unused
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }


    private fun SendMessage(){
        val text = textInput_edittext_chatLog.text.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/${fromUser.uid}/${toUser.uid}").push()
        val refReceiving = FirebaseDatabase.getInstance().getReference("/user_messages/${toUser.uid}/${fromUser.uid}").push()
        val latestMessagesRef = FirebaseDatabase.getInstance().getReference("/latest_messages/${fromUser.uid}/${toUser.uid}")
        val latestMessagesReceivingRef = FirebaseDatabase.getInstance().getReference("/latest_messages/${toUser.uid}/${fromUser.uid}")
        val message = ChatMessage(
            ref.key ?: "", text, toUser.uid, fromUser.uid, System.currentTimeMillis() /1000
        )

        ref.setValue(message)
            .addOnSuccessListener {
                Log.d(TAG,"Message sent successfully ${ref.key}")
            }

        if (toUser != fromUser) {
            refReceiving.setValue(message)
        }

        latestMessagesRef.setValue(message)
        latestMessagesReceivingRef.setValue(message)
    }
}

class ChatFromItem(val text:String):Item<GroupieViewHolder>()    {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_from_row.text = text
        Picasso.get().load(toUser.profileImageUrl).into(viewHolder.itemView.profilepicture_chat_to_row)
    }
    override fun getLayout(): Int {
    return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String):Item<GroupieViewHolder>()    {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(fromUser.profileImageUrl).into(viewHolder.itemView.profilepicture_chat_to_row)
        viewHolder.itemView.textView_chat_to_row.text = text
    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
