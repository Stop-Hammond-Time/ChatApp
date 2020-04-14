package com.example.chatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        register_button_login.setOnClickListener {
            Login()
        }

        register_link_login.setOnClickListener {
        finish()
        }

        profilePicture_login.setOnClickListener {
            Log.d("Main","Select profile picture clicked")
        }

    }
    private fun Login(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        Log.d("Main ", "Attempting login with \nEmail: $email \n password: $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
        .addOnCompleteListener{
            if (!it.isSuccessful) return@addOnCompleteListener

            Log.d("Main","successfully created user with uid: ${it.result?.user?.uid}")
        }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user for reason: ${it.message}")
            }
    }


}