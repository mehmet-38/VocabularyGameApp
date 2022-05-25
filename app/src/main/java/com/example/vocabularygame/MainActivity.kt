package com.example.vocabularygame

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.example.vocabularygame.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.toObject


class MainActivity : AppCompatActivity() {







    private lateinit var binding:ActivityMainBinding

    val PREFS_FILENAME = "com.example.vocabularygame"
    val KEY_MAIL = "MAIL"
    val KEY_PWD = "PWD"
    lateinit var emailView:EditText
    lateinit var passwodView:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailView = findViewById(R.id.emailLogin)
        passwodView = findViewById(R.id.passwordLogin)

        binding.register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        binding.signIn.setOnClickListener {
            signIn()
        }


        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        //Dosyadan okurken ilk parametre anahtar değer ikincisi ise okuma
        // işlemi başarısız olursa yada dosyada böyle bir değer yoksa
        // atanacak DEFAULT değerdir.
        //Toast.makeText(this,
        //    "Name : ${prefences.getString(KEY_MAIL,"")}\n" +
        //            "Age : ${prefences.getString(KEY_PWD,"")}\n"
        //    ,Toast.LENGTH_SHORT ).show()




    }

    override fun onStart() {
        super.onStart()

        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val mail:String ?= prefences.getString(KEY_MAIL,null)
        val pwd:String ?= prefences.getString(KEY_PWD,null)
        if(mail != null && pwd !=null){
            Toast.makeText(this, mail, Toast.LENGTH_SHORT).show()
            emailView.setText(mail)
            passwodView.setText(pwd)
            signIn()
        }
    }

    fun setPref(mail:String, password:String)
    {
        val prefences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor = prefences.edit()
        editor.putString(KEY_MAIL,mail)
        editor.putString(KEY_PWD,password)
        editor.apply() // Dosyaya yazılır.
    }

    private fun signIn() {
        val email:String =emailView.text.toString().trim{it<= ' '}
        val password:String =passwodView.text.toString().trim{it<= ' '}

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if (task.isSuccessful){
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    Toast.makeText(this,"You are logged in successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                    intent.putExtra("userMail",email)

                    val db = DatabaseControl().getKullanici("users",FirebaseAuth.getInstance().currentUser!!.uid).get()
                        .addOnSuccessListener {
                            intent.putExtra("userName",it.toObject<User>()!!.name)
                            setPref(email,password)
                        }
                        .addOnCompleteListener {
                            startActivity(intent)
                            finish()
                        }
                }
                else{
                    Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
    }
}