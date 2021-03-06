package com.example.paintbuddy.updateDrawing

import com.example.paintbuddy.conversion.StringConversions.Companion.convertPathToString
import com.example.paintbuddy.customClasses.CustomPaint
import com.example.paintbuddy.customClasses.CustomPath
import com.example.paintbuddy.firebaseClasses.DrawItem

class UploadDrawingInfo {

    companion object{
        //For CustomPath
        val drawList = ArrayList<DrawItem>()
        lateinit var BgColor : String
        lateinit var updater : UpdateOperations

        fun init(){
            BgColor = "#FFFFFF"
            drawList.clear()
            updater = UpdateOperations()
        }

        fun addDrawInfoToFirebase(pathList: ArrayList<CustomPath>, brushList: List<CustomPaint>, location: String = ""){
            val dSize = drawList.size
            val lSize = pathList.size

            when {
                lSize == 0 ->{
                    println("List Size Reduced to 0")
                    drawList.clear()

                    /**
                     * Clear the List
                     * */
                    updater.updateDrawInfo(drawList.toList(), location)
                }
                lSize == dSize -> {
                    println("List Size Same ${drawList[0].DrawPath}, ${pathList[0]}")

                    if (lSize > 0 && dSize > 0){
                        val item = DrawItem(convertPathToString(pathList[lSize - 1]),
                            java.lang.String.format(
                                "#%06X",
                                0xFFFFFF and  brushList[lSize - 1].color
                            ), brushList[lSize - 1].strokeWidth, brushList[lSize - 1].alpha, BgColor)
                        drawList[dSize - 1] = item

                        /**
                         * Updating the DrawItem
                         * A Index (dSize-1)
                         * */
                        updater.updateNodeToDrawingInfo(item, (dSize-1).toLong(), location)
                    }
                }
                lSize < dSize -> {

                        while (lSize != drawList.size){
                            /**
                             * Removing the item
                             * At index (drawList.size -1)
                             * From Firebase Database
                             * */
                            updater.deleteNodeFromDrawInfo((drawList.size -1).toLong(), location)

                            drawList.removeAt(drawList.size - 1)
                            println("List Size Reduced")
                        }


                }
                else -> {
                    val i = dSize
                    val j = lSize - 1

                    for (n in i..j){
                        val item = DrawItem(convertPathToString(pathList[n]), java.lang.String.format(
                            "#%06X",
                            0xFFFFFF and  brushList[lSize - 1].color
                        ), brushList[lSize - 1].strokeWidth, brushList[lSize - 1].alpha, BgColor)
                        drawList.add(item)

                        /**
                         * Adding the DrawItem
                         * To Firebase Database
                         * */
                        updater.addNodeToDrawingInfo(item, (n).toLong(), location)
                        println("List Size Increased")
                    }
                }
            }

        }
    }
}