package com.example.paintbuddy.updateDrawing

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.example.paintbuddy.constants.DatabaseLocations
import com.example.paintbuddy.constants.DatabaseLocations.Companion.DRAWING_LOCATION
import com.example.paintbuddy.constants.DatabaseLocations.Companion.SAVED_DRAWINGS
import com.example.paintbuddy.constants.StorageLocations
import com.example.paintbuddy.constants.StorageLocations.Companion.SAVED_DRAWING_THUMB
import com.example.paintbuddy.firebaseClasses.SavedItem
import com.example.paintbuddy.imageOperations.ImageResizer
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.util.*


class UpdateSavedDrawings {

    companion object {

        fun saveDrawing(drawingId: String, thumb : Bitmap,  title: String = "", itemCount: Long){
            uploadThumbnail(thumb, drawingId, title, itemCount)
        }

        private fun uploadThumbnail(bitmap: Bitmap, id : String, title: String, itemCount: Long){
            val ref = FirebaseStorage.getInstance().getReference("$SAVED_DRAWING_THUMB/$id")
            val thumb = ImageResizer.generateThumb(bitmap, 120000)
            ref.putBytes(bitmapToByteArray(thumb)).addOnSuccessListener {
                Log.d("SaveDrawing", "Uploaded thumbnail :: success")

                ref.downloadUrl.addOnSuccessListener {
                    saveDrawingToDatabase(id, "$it", title, itemCount)
                }
            }
        }

        private fun saveDrawingToDatabase(drawingId: String, thumb : String,  title: String, itemCount: Long){
            val saveToRef = FirebaseDatabase.getInstance().getReference("$SAVED_DRAWINGS/${FirebaseAuth.getInstance().uid}/").child(drawingId)
            val currentTime = System.currentTimeMillis()
            val savedItem = SavedItem(drawingId, "${FirebaseAuth.getInstance().uid}", title, thumb, currentTime, currentTime, itemCount)
            saveToRef.setValue(savedItem).addOnSuccessListener {
                Log.d("SaveDrawing", "Saved Drawing :: success")
            }
        }

        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)

            return stream.toByteArray()
        }



        fun updateSavedDrawingTitle(context: Context, newTitle: String, drawItem: SavedItem) {
            val saveToRef = FirebaseDatabase.getInstance().getReference("${DatabaseLocations.SAVED_DRAWINGS}/${drawItem.userId}/").child(drawItem.drawId)
            saveToRef.child("title").setValue(newTitle).addOnSuccessListener {
                Toast.makeText(context, "Rename Successful", Toast.LENGTH_SHORT).show()
            }
        }

        fun deleteDrawing(context: Context, userId: String, drawingId: String, thumbUri: String){
            val saveToRef = FirebaseDatabase.getInstance().getReference("${DatabaseLocations.SAVED_DRAWINGS}/${userId}/").child(drawingId)
            val drawRef = FirebaseDatabase.getInstance().getReference("$DRAWING_LOCATION/${userId}/").child(drawingId)

            val thumbRef = FirebaseStorage.getInstance().getReferenceFromUrl(thumbUri)

            Thread{
                drawRef.removeValue().addOnSuccessListener {
                    saveToRef.removeValue().addOnSuccessListener {
                        thumbRef.delete().addOnSuccessListener {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.start()
        }

        fun updateSavedDrawing(childCount: Int, userId: String, drawingId: String, thumb: String, newBitmap: Bitmap){
            val updateSaveToRef = FirebaseDatabase.getInstance().getReference(DatabaseLocations.SAVED_DRAWINGS).child(userId).child(drawingId)
            updateSaveToRef.child("nodeCount").setValue(childCount).addOnSuccessListener {
                println("Node Count Updated")
            }
            updateSaveToRef.child("lastModified").setValue(System.currentTimeMillis()).addOnSuccessListener {
                println("Last Modified Date Updated")
            }
            if (thumb != ""){
                val oldThumbRef = FirebaseStorage.getInstance().getReferenceFromUrl(thumb)

                val newThumbBitmap = ImageResizer.generateThumb(newBitmap, 120000)
                val ref = FirebaseStorage.getInstance().getReference("${StorageLocations.SAVED_DRAWING_THUMB}/${UUID.randomUUID()}")

                ref.putBytes(bitmapToByteArray(newThumbBitmap)).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener{
                        updateSaveToRef.child("thumbUri").setValue("$it").addOnSuccessListener {
                            oldThumbRef.delete().addOnSuccessListener {
                                println("Thumbnail Updated")
                            }
                        }
                    }
                }
            }

        }
    }
}

