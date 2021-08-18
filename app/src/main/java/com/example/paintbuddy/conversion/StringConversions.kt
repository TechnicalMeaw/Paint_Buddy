package com.example.paintbuddy.conversion

import android.util.Base64
import com.example.paintbuddy.Compressor
import com.example.paintbuddy.conversion.PathConversions.*
import com.example.paintbuddy.customClasses.CustomPaint
import com.example.paintbuddy.customClasses.CustomPath

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

        fun convertPathToString(path: CustomPath) : String{
            val byteArray = encodeToByteArray(path);
            return convertToBase64String(byteArray);
        }

        fun convertStringToPath(string: String) : CustomPath {
            val decompressedArray = convertToByteArray(string)
            return decodeToCustomPath(decompressedArray)
        }

        fun convertBrushToString(brush: CustomPaint) : String{
            val byteArray = encodeToByteArray(brush)
            return  convertToBase64String(byteArray)
        }

        fun convertStringToBrush(string: String) : CustomPaint {
            val decompressedArray = convertToByteArray(string)
            return decodeToCustomPaint(decompressedArray)
        }

    }
}