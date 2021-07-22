package com.example.paintbuddy

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class UploadBitmap {

    companion object{
        private const val TAG = "UploadOperations"

        fun uploadImageToFirebase(bitmap: Bitmap){
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("images/$filename")

            ref.putBytes(bitmapToByteArray(bitmap)).addOnSuccessListener {
                Log.d(TAG, "Bitmap Successfully Uploaded" )

                ref.downloadUrl.addOnSuccessListener{
                    UpdateOperations.updateDrawing(it.toString())
                }
            }
        }

        private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)

            return stream.toByteArray()
        }
    }
}