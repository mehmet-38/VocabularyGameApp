package com.example.vocabularygame
import android.content.Intent
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

class WordsControl (var context: AppCompatActivity){
    lateinit var liveWordsList: MutableLiveData<List<WordItem>>
    init{
        liveWordsList= MutableLiveData()
    }

    public fun getLiveWordsObserver(): MutableLiveData<List<WordItem>> {
        return liveWordsList
    }

    // FireStore users collectionundaki verileri liveWordsList'e post eder.
    public fun makeFireStoreCall() {
        val gson = Gson()
        val db = Firebase.firestore
        db.collection("words")
            .get()
            .addOnSuccessListener {
                var wordList = ArrayList<WordItem>()

                var i:Int = 0
                for (document in it)
                {
                    val wordItem = WordItem(document.id, document.toObject<Word>())
                    wordList.add(wordItem)

                }

                wordList.remove(wordList.first())
                liveWordsList.postValue(wordList)
            }
            .addOnFailureListener {
               liveWordsList.postValue(null)
            }
    }

    public fun getWord(soru:TextView,cevaps: List<TextView>, dogruCevap:TextView){
        getLiveWordsObserver().observe(context, Observer { wordItems->
            if (wordItems!=null){

                var selectedWordItem = wordItems[Random(Calendar.getInstance().timeInMillis).nextInt(0,wordItems.count()-1)]
                var wrongWordItem1:WordItem? = null
                var wrongWordItem2:WordItem? = null
                while (true)
                {
                    wrongWordItem1 =
                        wordItems[Random(Calendar.getInstance().timeInMillis).nextInt(0,wordItems.count()-1)]
                    wrongWordItem2 =
                        wordItems[Random(Calendar.getInstance().timeInMillis).nextInt(0,wordItems.count()-1)]
                    if(wrongWordItem1.id != selectedWordItem.id && (wrongWordItem2.id != selectedWordItem.id && wrongWordItem2.id != wrongWordItem1.id))
                        break
                }

                dogruCevap.text = selectedWordItem.word.ingilizce
                soru.text = selectedWordItem.word.turkce
                val shufledCevaps = cevaps.shuffled(Random(Calendar.getInstance().timeInMillis))
                shufledCevaps[0].text = wrongWordItem1?.word!!.ingilizce
                shufledCevaps[1].text = selectedWordItem.word.ingilizce
                shufledCevaps[2].text = wrongWordItem2?.word!!.ingilizce

            }
            else{
                Toast.makeText(context,"Error in getting word list",Toast.LENGTH_SHORT).show()
            }

        })

        makeFireStoreCall()
    }
}
