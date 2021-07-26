package com.example.paintbuddy

import com.example.paintbuddy.StringConversions.Companion.convertPathToString
import com.example.paintbuddy.UpdateOperations.Companion.updateDrawInfo
import com.example.paintbuddy.UpdateOperations.Companion.updateScreenResolution


class UploadDrawingInfo {
    companion object{
        //For CustomPath
        var drawList : ArrayList<DrawItem> = ArrayList()
        var color = "#000000"
        var BgColor : String = "#FFFFFF"

        fun addDrawInfoToFirebase(pathList: ArrayList<CustomPath>, stroke: Float, alpha: Int){
            val lSize = pathList.size
            val dSize = drawList.size
            when {
                lSize == dSize -> {
                    println("List Size Same")
                    if (lSize > 0 && dSize > 0){
                        val item = DrawItem(convertPathToString(pathList[lSize - 1]), color, stroke, alpha, BgColor)
                        drawList[dSize - 1] = item
                    }
                }
                lSize < dSize -> {
                    while (lSize != dSize){
                        drawList.removeAt(dSize - 1)
                        println("List Size Reduced")
                    }
                }
                else -> {
                    val j = lSize - 1
                    val i = dSize

                    for (n in i..j){
                        val item = DrawItem(convertPathToString(pathList[i]), color, stroke, alpha, BgColor)
                        drawList.add(item)
                    }
                }
            }


            updateDrawInfo(drawList.toList())
        }
    }
}