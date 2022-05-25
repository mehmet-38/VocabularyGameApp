package com.example.vocabularygame
import android.content.Intent
import android.content.IntentSender
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import org.w3c.dom.Document
import org.w3c.dom.Text
import java.io.DataOutput
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class ScoresControl (var context: AppCompatActivity){
    lateinit var liveScoresList: MutableLiveData<List<ScoreItem>>
    init{
        liveScoresList= MutableLiveData()
    }

    public fun getLiveScoresObserver(): MutableLiveData<List<ScoreItem>> {
        return liveScoresList
    }

    // FireStore scores collectionundaki verileri liveScoresList'e post eder.
    public fun makeFireStoreCall() {
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

    public fun addScore(newScoreItem: ScoreItem){
        var db = Firebase.firestore
        db.collection("scores").document(newScoreItem.id).set(newScoreItem.score)


      /*  var counterRef = DatabaseControl().getCounter("scores")
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
           */
    }
    public fun getScores(){
        getLiveScoresObserver().observe(context, Observer { scoreItems->
            if (scoreItems!=null){

                val sortedScores = scoreItems.sortedWith(compareBy<ScoreItem> { it.score.score }.thenBy { it.score.timestamp }.thenBy { it.score.name })
                Toast.makeText(context, "best score: ${sortedScores[0]}", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Error in getting word list", Toast.LENGTH_SHORT).show()
            }

        })
        makeFireStoreCall()
    }

    public fun getScore(id:String, view: TextView)
    {
        getLiveScoresObserver().observe(context, Observer { scoreItems->
            if (scoreItems!=null){

                var myScore:ScoreItem?=null
                for (scoreItem in scoreItems)
                {
                    if(scoreItem.id == id){
                        myScore = scoreItem
                        break
                    }
                }

                if(myScore != null)
                    view.text = myScore?.score!!.score.toString()
                else
                    view.text = "0"

            }
            else{
                Toast.makeText(context,"Error in getting word list", Toast.LENGTH_SHORT).show()
            }

        })
        makeFireStoreCall()
    }
}