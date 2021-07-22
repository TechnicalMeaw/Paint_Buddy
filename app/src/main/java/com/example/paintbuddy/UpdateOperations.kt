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
        private val ref = FirebaseDatabase.getInstance().getReference("/paths/")
        var imageItem : ImageItem? = null

        fun getCurrentValue(){
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("bitmapUri")){
                        imageItem = snapshot.getValue(ImageItem::class.java)!!
                        Log.d("UpdateOperations", "Data retrieved")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("UpdateOperations", "Error!")
                }
            })
        }

        fun updateDrawing(url: String){
            val item = ImageItem(url)

            if (imageItem!= null && imageItem!!.bitmapUri != ""){
                val photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageItem!!.bitmapUri.toString())
                photoRef.delete().addOnSuccessListener {
                    Log.d("UpdateOperations", "Deleted :: Success")

                }
            }

            ref.setValue(item).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated drawing value")
            }

        }
    }
}