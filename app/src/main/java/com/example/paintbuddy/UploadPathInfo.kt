package com.example.paintbuddy

import com.example.paintbuddy.PathConversions.encodeToByteArray
import com.example.paintbuddy.StringConversions.Companion.convertToBase64String

class UploadPathInfo {
    companion object{
        fun uploadPath(path: CustomPath){

            val byteArray = encodeToByteArray(path);
            val string = convertToBase64String(byteArray);
            

        }
    }
}