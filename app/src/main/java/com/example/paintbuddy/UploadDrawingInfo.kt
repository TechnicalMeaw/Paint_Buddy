package com.example.paintbuddy

import com.example.paintbuddy.StringConversions.Companion.convertPathToString
import com.example.paintbuddy.UpdateOperations.Companion.addNodeToDrawingInfo
import com.example.paintbuddy.UpdateOperations.Companion.deleteNodeFromDrawInfo
import com.example.paintbuddy.UpdateOperations.Companion.updateDrawInfo
import com.example.paintbuddy.UpdateOperations.Companion.updateNodeToDrawingInfo


class UploadDrawingInfo {
    companion object{
        //For CustomPath
        var drawList : ArrayList<DrawItem> = ArrayList()
        var color = "#000000"
        var BgColor : String = "#FFFFFF"

        fun addDrawInfoToFirebase(pathList: ArrayList<CustomPath>, stroke: Float, alpha: Int){
            val lSize = pathList.size
            val dSize = drawList.size

            if (dSize == 0){
                updateDrawInfo(drawList.toList())
            }

            when {
                lSize == 0 ->{
                    drawList.clear()
                    updateDrawInfo(drawList.toList())
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
                        updateNodeToDrawingInfo(item, (dSize-1).toLong())
                    }
                }
                lSize < dSize -> {
                    while (lSize != drawList.size){
                        /**
                        * Removing the item
                        * At index (drawList.size -1)
                        * From Firebase Database
                        * */
                        deleteNodeFromDrawInfo((drawList.size -1).toLong())

                        drawList.removeAt(drawList.size - 1)
                        println("List Size Reduced")
                    }
                }
                else -> {
                    val i = dSize
                    val j = lSize - 1

                    for (n in i..j){
                        val item = DrawItem(convertPathToString(pathList[i]), color, stroke, alpha, BgColor)
                        drawList.add(item)

                        /**
                        * Adding the DrawItem
                        * To Firebase Database
                        * */
                        addNodeToDrawingInfo(item, (n).toLong())
                    }
                }
            }

        }
    }
}