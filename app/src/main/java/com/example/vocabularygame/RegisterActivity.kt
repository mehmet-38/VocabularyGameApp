package com.example.vocabularygame
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.vocabularygame.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import org.json.JSONObject
import org.json.JSONTokener



class RegisterActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityRegisterBinding

    //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            callRegister()
        }

    }

    fun callRegister()
    {
        val email:String =binding.email.text.toString()
        val password:String =binding.registerPassword.text.toString()
        val name:String = binding.registerName.text.toString()
        val telNo:String = binding.telNo.text.toString()

        var newUser = User(name,email,password,telNo)

        val userControl = UsersControl(this)

        userControl.addUser(newUser)


    }









}