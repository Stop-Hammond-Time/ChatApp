package com.example.chatapp.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp.chatlog.ChatLogActivity
import com.example.chatapp.R
import com.example.chatapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select recipient"

        fetchUsers()
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach{
                    Log.d("NewMessage",it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)

                    startActivity(intent)
                    finish()
                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

}

class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textView_user_row.text = user.username
        viewHolder.itemView.description_textView_user_row.text = user.uid
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profilePicture_user_row)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}