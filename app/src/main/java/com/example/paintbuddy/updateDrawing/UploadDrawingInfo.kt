package com.example.paintbuddy.updateDrawing

import com.example.paintbuddy.firebaseClasses.DrawItem
import com.example.paintbuddy.conversion.StringConversions.Companion.convertPathToString
import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.addNodeToDrawingInfo
import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.deleteNodeFromDrawInfo
import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.updateDrawInfo
import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.updateNodeToDrawingInfo
import com.example.paintbuddy.customClasses.CustomPath
import javax.inject.Singleton

class UploadDrawingInfo {
        //For CustomPath
        var drawList : ArrayList<DrawItem> = ArrayList()
        var color = "#000000"
        var BgColor : String = "#FFFFFF"


        fun addDrawInfoToFirebase(pathList: ArrayList<CustomPath>, stroke: Float, alpha: Int, location: String = ""){
            val lSize = pathList.size
            val dSize = drawList.size

            if (dSize == 0 && lSize <3){
                /**
                 * Clear the List
                 * */
                updateDrawInfo(drawList.toList(), location)
                println("List Size Reduced to 0")
            }

            when {
                lSize == 0 ->{
                    println("List Size Reduced to 0")
                    drawList.clear()

                    /**
                     * Clear the List
                     * */
                    updateDrawInfo(drawList.toList(), location)
                }
                lSize == dSize -> {
                    println("List Size Same")
                    if (lSize > 0 && dSize > 0){
                        val item = DrawItem(convertPathToString(pathList[lSize - 1]), color, stroke, alpha, BgColor)
                        drawList[dSize - 1] = item

                        /**
                         * Updating the DrawItem
                         * A Index (dSize-1)
                         * */
                        updateNodeToDrawingInfo(item, (dSize-1).toLong(), location)
                    }
                }
                lSize < dSize -> {
                    while (lSize != drawList.size){
                        /**
                        * Removing the item
                        * At index (drawList.size -1)
                        * From Firebase Database
                        * */
                        deleteNodeFromDrawInfo((drawList.size -1).toLong(), location)

                        drawList.removeAt(drawList.size - 1)
                        println("List Size Reduced")
                    }
                }
                else -> {
                    val i = dSize
                    val j = lSize - 1

                    for (n in i..j){
                        val item = DrawItem(convertPathToString(pathList[n]), color, stroke, alpha, BgColor)
                        drawList.add(item)

                        /**
                        * Adding the DrawItem
                        * To Firebase Database
                        * */
                        addNodeToDrawingInfo(item, (n).toLong(), location)
                        println("List Size Increased")
                    }
                }
            }

        }
}