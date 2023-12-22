package com.abdullah.kotlinjavainstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.abdullah.kotlinjavainstagram.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    fun signInClicked(view: View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.equals("")|| password.equals("")){
            Toast.makeText(this@MainActivity,"Email veya şifre eksik girildi",Toast.LENGTH_LONG)
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }

    fun signUpClicked(view:View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.equals("")|| password.equals("")){
            Toast.makeText(this@MainActivity,"Email veya şifre eksik girildi",Toast.LENGTH_LONG)
        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }


    }


}