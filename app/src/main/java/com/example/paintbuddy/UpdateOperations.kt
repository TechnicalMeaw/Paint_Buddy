package com.example.paintbuddy

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

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

//        private val pathRef = FirebaseDatabase.getInstance().getReference("/pathInfo/")
//        fun updatePathInfo(list: ArrayList<String>){
//            if (list.isNotEmpty()){
//                pathRef.setValue(list.toList()).addOnSuccessListener {
//                    Log.d("UpdateOperations", "Successfully updated pathInfo value")
//                }
//            }
//        }
//
//        private val brushRef = FirebaseDatabase.getInstance().getReference("/brushInfo/")
//        fun updateBrushInfo(list: ArrayList<String>){
//            if (list.isNotEmpty()){
//                brushRef.setValue(list.toList()).addOnSuccessListener {
//                    Log.d("UpdateOperations", "Successfully updated brushInfo value")
//                }
//            }
//        }

        private val drawRef = FirebaseDatabase.getInstance().getReference("/DrawInfo/")
        fun updateDrawInfo(itemList: List<DrawItem>){

            drawRef.setValue(itemList).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated brushInfo value")
            }
        }

        private val scrRef = FirebaseDatabase.getInstance().getReference("/ScreenRes/")
        fun updateScreenResolution(width: Int, height: Int){
            val map : HashMap<String, Int> = HashMap()
            map["width"] = width
            map["height"] = height

            scrRef.setValue(map).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated Screen Resolution")
            }
        }
    }
}