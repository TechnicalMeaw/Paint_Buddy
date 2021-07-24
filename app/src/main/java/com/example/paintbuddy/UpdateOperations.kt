package com.example.paintbuddy

import android.util.Log
import com.example.paintbuddy.CanvasView.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

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