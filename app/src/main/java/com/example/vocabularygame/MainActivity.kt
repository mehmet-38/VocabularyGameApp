package com.example.vocabularygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.vocabularygame.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        binding.signIn.setOnClickListener {
            signIn()
        }


    }

    private fun signIn() {
        val email:String =binding.email.text.toString().trim{it<= ' '}
        val password:String =binding.password.text.toString().trim{it<= ' '}

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    Toast.makeText(this,"You are logged in successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("user_id",FirebaseAuth.getInstance().currentUser!!.uid)
                    intent.putExtra("email_id",email)
                    startActivity(intent)
                    finish()

                }
                else{
                    Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
    }
}