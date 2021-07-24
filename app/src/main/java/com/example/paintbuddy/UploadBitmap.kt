package com.example.paintbuddy

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*
import android.util.Base64;

class UploadBitmap {

    companion object{
        private const val TAG = "UploadOperations"

        fun uploadImageToFirebase(bitmap: Bitmap){
            UpdateOperations.updateDrawing(convertToBase64String(bitmap))

        }

        private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val reducedBitmap = ImageResizer.reduceBitmapSize(bitmap, 250000)
            reducedBitmap.setHasAlpha(true)
            val stream = ByteArrayOutputStream()

            reducedBitmap.compress(Bitmap.CompressFormat.PNG, 60, stream)

            return stream.toByteArray()
        }

        private fun compressByteArray(byteArray: ByteArray): ByteArray {
            return Compressor.compress(byteArray)
        }

        private fun convertToBase64String(bitmap: Bitmap): String {
            val byteImage = compressByteArray(bitmapToByteArray(bitmap))

            return Base64.encodeToString(byteImage, Base64.DEFAULT)
        }
    }
}