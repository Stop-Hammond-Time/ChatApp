package com.example.chatapp.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.chatapp.R
import com.example.chatapp.chatlog.ChatLogActivity
import com.example.chatapp.login_register.RegisterActivity
import com.example.chatapp.messages.NewMessageActivity.Companion.USER_KEY
import com.example.chatapp.models.ChatMessage
import com.example.chatapp.models.LatestMessage
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessages : AppCompatActivity() {
    companion object{
        var thisUser = User()
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        verifyUserIsLoggedIn()

        recyclerview_latestmessages.adapter = adapter
        recyclerview_latestmessages.addItemDecoration(DividerItemDecoration(
            this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->

            val intent = Intent(view.context, ChatLogActivity::class.java)
            val row = item as LatestMessage
            intent.putExtra(USER_KEY, row.toUser)
            startActivity(intent)

        }

        listenForMessages()
        refreshMessages()
    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{Log.d("LatestMessages",it.text)}
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessage(it))
        }
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance()
            .getReference("/latest_messages/${FirebaseAuth.getInstance().uid}")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
//                if (chatMessage.fromId == thisUser.uid ) return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshMessages()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshMessages()

            }
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            signOut()
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    thisUser = p0.getValue(User::class.java)!!
                    Log.d("LatestMessages", "Current user: ${thisUser.username}")
                }

            })
    }

    private fun signOut(){
        val intent = Intent(this,
            RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //Menu controls
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_new_message ->{
                //start new message activity
                val intent = Intent(this,
                    NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                signOut()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
