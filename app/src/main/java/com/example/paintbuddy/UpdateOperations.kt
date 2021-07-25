package com.example.paintbuddy

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class UpdateOperations {
    companion object{
        var bgColor = R.color.eraser
        private val ref = FirebaseDatabase.getInstance().getReference("/paths/")

        fun updateDrawing(imageString: String){
            if (imageString != ""){
                val item = ImageItem(imageString, bgColor)
                ref.setValue(item).addOnSuccessListener {
                    Log.d("UpdateOperations", "Successfully updated drawing value")
                }
            }

        }
    }
}