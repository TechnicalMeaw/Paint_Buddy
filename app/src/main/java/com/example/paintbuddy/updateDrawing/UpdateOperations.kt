package com.example.paintbuddy.updateDrawing

import android.util.Log
import com.example.paintbuddy.R
import com.example.paintbuddy.constants.DatabaseLocations.Companion.DRAWING_LOCATION
import com.example.paintbuddy.constants.DatabaseLocations.Companion.SCREEN_RES_LOCATION
import com.example.paintbuddy.firebaseClasses.DrawItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Singleton

class UpdateOperations {
    companion object{

        private val drawRef = FirebaseDatabase.getInstance().getReference("$DRAWING_LOCATION/${FirebaseAuth.getInstance().uid}/")
        fun updateDrawInfo(itemList: List<DrawItem>, id: String){
            /**
            * If the list is empty then
            * First set the value of node to -1
            * And then update the empty list to delete all nodes
            */
            if (itemList.isEmpty()){
                drawRef.child(id).child("0").setValue(-1)
            }

            /**
            * Update the list to the Node
            */
            drawRef.setValue(itemList).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully updated brushInfo value")
            }
        }


        private val drawPushRef = FirebaseDatabase.getInstance().getReference("$DRAWING_LOCATION/${FirebaseAuth.getInstance().uid}/").push()
        fun addNodeToDrawingInfo(item: DrawItem, index: Long, id: String){
            /**
            * Create a HashMap of Index
            * and DrawItem
            * */
            val map : HashMap<Long, DrawItem> = HashMap()
            map[index] = item

            /**
            * Push the node to database
            */
            drawPushRef.child(id).setValue(map).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully added brushInfo value")
            }
        }


        fun deleteNodeFromDrawInfo(index: Long, id: String){
            /**
            * Delete the node at index
            * */
            drawRef.child(id).child("$index").removeValue()
        }


        fun updateNodeToDrawingInfo(item: DrawItem, index: Long, id: String){
            /**
            * Update the item to database
            * At the index position
            */
            drawRef.child(id).child("$index").setValue(item).addOnSuccessListener {
                Log.d("UpdateOperations", "Successfully added brushInfo value")
            }
        }


        private val scrRef = FirebaseDatabase.getInstance().getReference("$SCREEN_RES_LOCATION/${FirebaseAuth.getInstance().uid}")
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