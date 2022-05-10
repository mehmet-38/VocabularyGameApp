package com.example.vocabularygame

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class UsersControl (var context: AppCompatActivity) {



    lateinit var liveUserList: MutableLiveData<List<User>>
    init{
        liveUserList= MutableLiveData()
    }

    public fun getLiveUserObserver(): MutableLiveData<List<User>> {
        return liveUserList
    }

    // FireStore users collectionundaki verileri liveUserList'e post eder.
    public fun makeFireStoreCall() {
        val gson = Gson()
        val db = Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                var userList = ArrayList<User>()
                for (document in result) {
                    userList.add(gson.fromJson(document.data.toString(),User::class.java))
                }
                liveUserList.postValue(userList)

            }
            .addOnFailureListener {
                liveUserList.postValue(null)
            }
    }

//------------------------------------------------------------------------------------
    public fun addUser(newUser:User){
        getLiveUserObserver().observe(context, Observer { users->
            if (users!=null){

                var validPhone:Boolean = true
                for (user in users){
                    if(user.telNo == newUser.telNo)
                        validPhone = false
                }

                Toast.makeText(context,"Phone Valid: ${validPhone}",Toast.LENGTH_SHORT).show()
                if(validPhone)
                    this.addAuth(newUser)
            }
            else{
                Toast.makeText(context,"Error in getting user list",Toast.LENGTH_SHORT).show()
            }

        })
        makeFireStoreCall()
    }

    private fun addAuth(newUser:User) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(newUser.email.toString(),newUser.password.toString())
            .addOnCompleteListener(
                OnCompleteListener <AuthResult> { task->
                    if (task.isSuccessful){

                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        Toast.makeText(context,"You are registered successfully",Toast.LENGTH_SHORT).show()
                        addFireStoreData(firebaseUser.uid, newUser)

                        val intent = Intent(context,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        addFireStoreData(firebaseUser.uid,newUser)
                        context.startActivity(intent)
                        context.finish()

                    }
                    else{
                        Toast.makeText(context,task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                })

    }

    private fun addFireStoreData(auth_uid:String, newUser: User) {

        var db = Firebase.firestore
        db.collection("users").document(auth_uid).set(newUser)



        var counterRef = DatabaseControl().getCounter("users")
        counterRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val stat = document.toObject<DatabaseControl.DocStats>()
                    val newStat = DatabaseControl.DocStats(stat?.count!! +1)
                    counterRef.set(newStat)

                }
                else {
                    print( "No such document")
                }
            }

    }

//-------------------------------------------------------------------------------------



}