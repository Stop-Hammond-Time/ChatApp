package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        register_button_registration.setOnClickListener {
            val email = email_edittext_registration.text.toString()
            val password = password_edittext_registration.text.toString()

            Log.d("Main Activity", "\nEmail is $email \n password is $password")
        }

        loginlink_registration.setOnClickListener {
            Log.d("Main Activity","login link clicked")
        }

        loginlink_registration.setOnClickListener {
            //start new activity
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
