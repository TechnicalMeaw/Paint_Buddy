package com.example.paintbuddy.updateDrawing

import android.graphics.Bitmap
import android.util.Log
import com.example.paintbuddy.constants.DatabaseLocations.Companion.SAVED_DRAWINGS
import com.example.paintbuddy.constants.StorageLocations.Companion.SAVED_DRAWING_THUMB
import com.example.paintbuddy.firebaseClasses.SavedItem
import com.example.paintbuddy.imageOperations.ImageResizer
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class UpdateSavedDrawings {

    companion object {

        fun saveDrawing(drawingId: String, thumb : Bitmap,  title: String = ""){
            uploadThumbnail(thumb, drawingId, title)
        }

        private fun uploadThumbnail(bitmap: Bitmap, id : String, title: String){
            val ref = FirebaseStorage.getInstance().getReference("$SAVED_DRAWING_THUMB/$id")
            val thumb = ImageResizer.generateThumb(bitmap, 25000)
            ref.putBytes(bitmapToByteArray(thumb)).addOnSuccessListener {
                Log.d("SaveDrawing", "Uploaded thumbnail :: success")

                ref.downloadUrl.addOnSuccessListener {
                    saveDrawingToDatabase(id, "$it", title)
                }
            }
        }

        private fun saveDrawingToDatabase(drawingId: String, thumb : String,  title: String){
            val saveToRef = FirebaseDatabase.getInstance().getReference("$SAVED_DRAWINGS/${FirebaseAuth.getInstance().uid}/").child(drawingId)
            val currentTime = System.currentTimeMillis()
            val savedItem = SavedItem(drawingId, "${FirebaseAuth.getInstance().uid}", title, thumb, currentTime, currentTime)
            saveToRef.setValue(savedItem).addOnSuccessListener {
                Log.d("SaveDrawing", "Saved Drawing :: success")
            }
        }

        private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)

            return stream.toByteArray()
        }
    }
}

