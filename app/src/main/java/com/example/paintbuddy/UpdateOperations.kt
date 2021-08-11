package com.example.paintbuddy

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class UpdateOperations {
    companion object{
        var bgColor = R.color.eraser

        private val drawRef = FirebaseDatabase.getInstance().getReference("/DrawInfo/")
        fun updateDrawInfo(itemList: List<DrawItem>){
            /**
            * If the list is empty then
            * First set the value of node to -1
            * And then update the empty list to delete all nodes
            */
            if (itemList.isEmpty()){
                drawRef.child("0").setValue(-1)
            }

            /**
            * Update the list to the Node
            */
            drawRef.setValue(itemList).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated brushInfo value")
            }
        }

        private val drawPushRef = FirebaseDatabase.getInstance().getReference("/DrawInfo/").push()
        fun addNodeToDrawingInfo(item: DrawItem, index: Long){
            /**
            * Create a HashMap of Index
            * and DrawItem
            * */
            val map : HashMap<Long, DrawItem> = HashMap()
            map[index] = item

            /**
            * Push the node to database
            */
            drawPushRef.setValue(map).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully added brushInfo value")
            }
        }

        fun deleteNodeFromDrawInfo(index: Long){
            /**
            * Delete the node at index
            * */
            drawRef.child("$index").removeValue()
        }

        fun updateNodeToDrawingInfo(item: DrawItem, index: Long){
            /**
            * Update the item to database
            * At the index position
            */
            drawRef.child("$index").setValue(item).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully added brushInfo value")
            }
        }

        private val scrRef = FirebaseDatabase.getInstance().getReference("/ScreenRes/")
        fun updateScreenResolution(width: Int, height: Int){
            /**
            * Create a HashMap of
            * Width and
            * Height
            * To push the info to ScreenRes
            * */

            val map : HashMap<String, Int> = HashMap()
            map["width"] = width
            map["height"] = height

            /**
            * Update the ScreenRes node
            */
            scrRef.setValue(map).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated Screen Resolution")
            }
        }
    }
}