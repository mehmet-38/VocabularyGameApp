package com.example.vocabularygame

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.services.events.TimeStamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.time.Instant.now
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class GameActivity : AppCompatActivity() {

    lateinit var soru:TextView
    lateinit var dogruCevap:TextView
    lateinit var cevap0:Button
    lateinit var cevap1:Button
    lateinit var cevap2:Button
    lateinit var scoreView: TextView
    lateinit var ekleButton:Button
    lateinit var ingEkle :EditText
    lateinit var  turkEkle:EditText
    lateinit var getRandomButton:Button


    lateinit var currentScore:ScoreItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val userId =  intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName")
        val userScore = intent.getIntExtra("userScore",0)



        currentScore = ScoreItem(userId!!,Score(userScore,userName,null))

        soru = findViewById(R.id.turkceKelime)
        dogruCevap = findViewById(R.id.dogruCevap)
        cevap0 = findViewById(R.id.cevap0)
        cevap1 = findViewById(R.id.cevap1)
        cevap2 = findViewById(R.id.cevap2)
        scoreView = findViewById(R.id.scoreView)
        ClearGame()
        val cevapList = listOf(cevap0,cevap1,cevap2)
        getRandomButton = findViewById(R.id.getRandomButton)
        ekleButton = findViewById(R.id.ekleButton)
        ingEkle = findViewById(R.id.ekleIngilizce)
        turkEkle = findViewById(R.id.ekleTurkce)
        ekleButton.setOnClickListener {

            val newWord = Word(ingEkle.text.toString(), turkEkle.text.toString())
            add(newWord)
            ingEkle.text.clear()
            turkEkle.text.clear()

        }

    }

    override fun onStart() {
        super.onStart()
        GetRandomSoru(getRandomButton)
    }

    public final fun GetRandomSoru(view:View)
    {
        val cevapList = listOf(cevap0,cevap1,cevap2)
        WordsControl(this).getWord(soru,cevapList, dogruCevap)
        ClearGame()
    }

    fun ClearGame()
    {
        cevap0.setBackgroundColor(Color.parseColor("#673AB7"))
        cevap1.setBackgroundColor(Color.parseColor("#673AB7"))
        cevap2.setBackgroundColor(Color.parseColor("#673AB7"))
        cevap0.isEnabled=true;
        cevap1.isEnabled=true;
        cevap2.isEnabled=true;
    }

    var totalScore:Int = 0
    fun addScore()
    {
        totalScore+=5;
        scoreView.text = totalScore.toString()
    }

    public final fun cevapClick(view: View)
    {
        if(view is Button)
        {
            cevap0.isEnabled=false;
            cevap1.isEnabled=false;
            cevap2.isEnabled=false;
            var dogru = false
            if(view.text == dogruCevap.text)
                dogru = true

            if(dogru)
            {
                view.setBackgroundColor(Color.GREEN)
                addScore()
                Handler().postDelayed({ GetRandomSoru(getRandomButton) }, 1000)
            }

            else
            {
                view.setBackgroundColor(Color.RED)
                Toast.makeText(this, "Oyun Bitti", Toast.LENGTH_SHORT).show()

                if(totalScore > currentScore.score.score!!)
                {
                    Toast.makeText(this, "SCore GEcildi", Toast.LENGTH_SHORT).show()
                    val scoreItem = ScoreItem(
                        currentScore.id,
                        Score(totalScore,currentScore?.score!!.name, Timestamp.now())
                    )
                    ScoresControl(this).addScore(scoreItem)

                }
                Handler().postDelayed({ finish() }, 2000)
            }



        }

    }

    private fun add( newWord: Word) {

        var db = Firebase.firestore
        db.collection("words").document().set(newWord)



        var counterRef = DatabaseControl().getCounter("words")
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


}