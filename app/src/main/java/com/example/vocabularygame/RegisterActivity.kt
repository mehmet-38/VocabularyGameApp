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

    private lateinit var firebaseAuth: FirebaseAuth


    fun deneme()
    {
        val usersControl = UsersControl(this)
        usersControl.getLiveUserObserver().observe(this, Observer { users->
            if (users!=null){
                Toast.makeText(this,"users verileri cekildi. Boyut: ${users.count()}",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Error in getting list",Toast.LENGTH_SHORT).show()
            }

        })
        usersControl.makeFireStoreCall()
    }
    //


    private fun counter()
    {
        var db = DatabaseControl()

        var dr = db.getCounter("users")
        dr.get()
            .addOnSuccessListener { document ->
                if (document != null) {

                     val ss = document.toObject<DatabaseControl.DocStats>()

                    Toast.makeText(this,"count: ${ss?.count}",Toast.LENGTH_SHORT).show()
                } else {
                    print( "No such document")
                }
            }
            .addOnFailureListener { exception ->
                print( "get failed with " + exception)
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            //validateData()
            callRegister()
        }
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.deneme.setOnClickListener {
            deneme()
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

    private fun getData(kullaniciId:String){
        var db2 = DatabaseControl()
        var sr = db2.getKullanici("users",kullaniciId)
        sr.get().addOnSuccessListener { document ->
            if (document != null) {
                var veri = (document.toObject<User>())
            }

        }

    }







}