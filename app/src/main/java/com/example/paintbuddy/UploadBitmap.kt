package com.example.paintbuddy
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import com.example.paintbuddy.StringConversions.Companion.convertToBase64String

class UploadBitmap {

    companion object{
        private const val TAG = "UploadOperations"

        fun uploadImageToFirebase(bitmap: Bitmap){
            UpdateOperations.updateDrawing(convertToString(bitmap))

        }

        private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val reducedBitmap = ImageResizer.reduceBitmapSize(bitmap, 250000)
            val stream = ByteArrayOutputStream()

            reducedBitmap.compress(Bitmap.CompressFormat.PNG, 60, stream)

            return stream.toByteArray()
        }

        private fun convertToString(bitmap: Bitmap): String {
            return convertToBase64String(bitmapToByteArray(bitmap))
        }
    }
}