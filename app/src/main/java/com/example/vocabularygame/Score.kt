package com.example.vocabularygame

import com.google.firebase.Timestamp


data class Score(val score:Int ?= null, val name:String ?= null, val timestamp: Timestamp ?= null)
