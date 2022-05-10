package com.example.vocabularygame

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.vocabularygame.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding
    lateinit var currentUser:UserItem
    lateinit var userScore:Score
    lateinit var scoreView: TextView
    lateinit var nameView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scoreView = findViewById(R.id.profileScore)
        nameView = findViewById(R.id.profileName)
        val userId =  intent.getStringExtra("userId")
        val userMail =  intent.getStringExtra("userMail")
        val userName =  intent.getStringExtra("userName")
        val userPhone = intent.getStringExtra("userPhone")


        currentUser = UserItem(userId, User(userName,userMail,null,userPhone ))



        binding.logout.setOnClickListener {
              logOut()
          }


    }



    override fun onResume() {
        super.onResume()
        ScoresControl(this).getScore(currentUser.id!!,scoreView)
        nameView.text = currentUser.user!!.name
    }

    private fun logOut() {

        val PREFS_FILENAME = "com.example.vocabularygame"
        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        prefences.edit().clear().commit()
        FirebaseAuth.getInstance().signOut()

        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }



    public final fun OynaClick(view: View)
    {
        if(view is Button)
        {
            var intent = Intent(this, GameActivity::class.java)
            intent.putExtra("userId", currentUser.id)
            intent.putExtra("userName", currentUser.user!!.name)
            intent.putExtra("userScore", scoreView.text.toString().toIntOrNull())
            startActivity(intent)

        }
    }

}