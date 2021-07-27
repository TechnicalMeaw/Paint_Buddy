package com.example.paintbuddy

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class UpdateOperations {
    companion object{
        var bgColor = R.color.eraser

        private val drawRef = FirebaseDatabase.getInstance().getReference("/DrawInfo/")
        fun updateDrawInfo(itemList: List<DrawItem>){

            if (itemList.isEmpty()){
                drawRef.child("0").setValue(-1)
            }
            drawRef.setValue(itemList).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated brushInfo value")
            }
        }

        private val scrRef = FirebaseDatabase.getInstance().getReference("/ScreenRes/")
        fun updateScreenResolution(width: Int, height: Int){
            val map : HashMap<String, Int> = HashMap()
            map["width"] = width
            map["height"] = height

            scrRef.setValue(map).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated Screen Resolution")
            }
        }
    }
}