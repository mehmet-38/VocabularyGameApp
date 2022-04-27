package com.example.vocabularygame
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.DocumentReference



public class DatabaseControl {

     @IgnoreExtraProperties
     data class Kullanici(var name: String? = null) {

     }
    @IgnoreExtraProperties
    class DocStats( val count:Int? = null)
    {

    }
    fun getCounter(Collection:String):DocumentReference{
        var db = Firebase.firestore
        val docRef = db.collection(Collection).document("--stats--")

        return docRef
    }
     fun getKullanicilar(collectin: String, kullaniciId: String,):DocumentReference{

         var db = Firebase.firestore
         var getKul = db.collection(collectin).document(kullaniciId)

        return  getKul
     }




}


