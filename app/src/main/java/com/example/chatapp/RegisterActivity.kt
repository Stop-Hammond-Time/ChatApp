package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import android.provider.MediaStore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_registration.setOnClickListener {
            register()
        }

        loginlink_registration.setOnClickListener {
            Log.d("RegisterActivity","login link clicked")
            //start new activity
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        profilePicture_registration.setOnClickListener {
            Log.d("RegisterActivity","Select profile picture clicked")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }

    var selectedPhotoURI: Uri? = null


    //Set Image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotoURI = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoURI)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            profilePicture_registration.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            profilePicture_registration.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun register(){
        val email = email_edittext_registration.text.toString()
        val password = password_edittext_registration.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Enter email and password, dipshit...", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("RegisterActivity", "\nEmail is $email \n password is $password")

        //Create User
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegisterActivity","successfully created user with uid: ${it.result?.user?.uid}")
                uploadImageToFirebaseStorage()

                val intent = Intent(this,LatestMessages::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to create user for reason: ${it.message}")
                Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
            }

    }
    fun uploadImageToFirebaseStorage(){
        if (selectedPhotoURI==null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
        ref.putFile(selectedPhotoURI!!)
            .addOnSuccessListener { uri ->
                Log.d("RegisterActivity","Upload successful: ${uri.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener { 
                    Log.d("RegisterActivity","File location $it")
                    saveUserToDatabase(it.toString())
                }

            }
            .addOnFailureListener{
                Log.d("RegisterActivity","Upload failed")
            }
    }

    private fun saveUserToDatabase(imageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_edittext_registration.text.toString(),imageUrl)
        ref.setValue(user)
            .addOnSuccessListener { Log.d("RegisterActivity","Successfully created user in database") }
            .addOnFailureListener { Log.d("RegisterActivity","Failed to create user reason: ${it.message}")}
    }
}

class User(val uid:String, val username: String, val profileImageUrl: String){
    constructor() : this("","","")
}
