package com.example.paintbuddy

import android.graphics.Bitmap
import android.util.Base64

class StringConversions {
    companion object{

        fun convertToBase64String(byteArray: ByteArray): String {
            val compressedByteArray = Compressor.compress(byteArray)
            return Base64.encodeToString(compressedByteArray, Base64.DEFAULT)
        }

        fun convertToByteArray(string: String): ByteArray{
            val compressedByteArray = Base64.decode(string, Base64.DEFAULT)
            return Compressor.decompress(compressedByteArray)
        }

    }
}