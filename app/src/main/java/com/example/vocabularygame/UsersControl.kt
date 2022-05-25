package com.example.vocabularygame

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class UsersControl (var context: AppCompatActivity) {



    lateinit var liveUserList: MutableLiveData<List<UserItem>>
    init{
        liveUserList= MutableLiveData()
    }

    public fun getLiveUserObserver(): MutableLiveData<List<UserItem>> {
        return liveUserList
    }

    // FireStore users collectionundaki verileri liveUserList'e post eder.
    public fun makeFireStoreCall() {
        val gson = Gson()
        val db = Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->

                var userItemList = ArrayList<UserItem>()

                for (document in result) {

                    if(document == result.elementAt(0))
                        continue

                    var user =gson.fromJson(document.data.toString(),User::class.java)
                    var userItem = UserItem(document.id, user)

                    userItemList.add(userItem)
                }

                liveUserList.postValue(userItemList)

            }
            .addOnFailureListener {
                liveUserList.postValue(null)
            }
    }







// ------------------------------------------------------------------------------------

    lateinit var liveScoresList: MutableLiveData<List<ScoreItem>>
    init{
        liveScoresList= MutableLiveData()
    }

    public fun getLiveScoresObserver(): MutableLiveData<List<ScoreItem>> {
        return liveScoresList
    }

    // FireStore scores collectionundaki verileri liveScoresList'e post eder.
    public fun makeFireStoreScoresCall() {
        val gson = Gson()
        val db = Firebase.firestore
        db.collection("scores")
            .get()
            .addOnSuccessListener {
                var scoreList = ArrayList<ScoreItem>()

                for (document in it)
                {
                    val scoreItem = ScoreItem(document.id, document.toObject<Score>())
                    scoreList.add(scoreItem)

                }

                scoreList.remove(scoreList.first())
                liveScoresList.postValue(scoreList)
            }
            .addOnFailureListener {
                liveScoresList.postValue(null)
            }
    }



//------------------------------------------------------------------------------------
    public fun addUser(newUser:User){
        getLiveUserObserver().observe(context, Observer { userItems->
            if (userItems!=null){

                var validPhone:Boolean = true
                for (userItem in userItems){
                    if(userItem.user!!.telNo == newUser.telNo)
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

    public fun setViewMatchesUsers( recyclerView: RecyclerView, userContacts: List<ContactDTO>)
    {
        val toplistFriends: MutableList<ScoreItem> = ArrayList()

        getLiveUserObserver().observe(context, Observer { userItems->
            if (userItems!=null ){


                for (userItem in userItems){
                    var i = 0
                    for(uc in userContacts)
                    {
                        val ucNum = userContacts[i++].number
                            .replace("-", "")
                            .replace("(","")
                            .replace(")","")
                            .replace("+90","0")
                            .replace(" ", "")

                        if(userItem.user!!.telNo == ucNum)
                        {
                            getLiveScoresObserver().observe(context, Observer { scoresItems->
                                for(scoreItem in scoresItems)
                                {
                                   if(scoreItem.id == userItem.id)
                                   {
                                       toplistFriends.add(scoreItem)
                                       break
                                   }

                                }
                            })
                            break
                        }
                    }
                }

                recyclerView.adapter =  RecyclerAdapter( toplistFriends )

            }
            else{
                Toast.makeText(context,"Error in getting user list",Toast.LENGTH_SHORT).show()
            }
        })

        makeFireStoreScoresCall()
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