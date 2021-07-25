package com.example.paintbuddy

import android.graphics.Paint
import com.example.paintbuddy.StringConversions.Companion.convertBrushToString
import com.example.paintbuddy.StringConversions.Companion.convertPathToString
import com.example.paintbuddy.UpdateOperations.Companion.updateBrushInfo
import com.example.paintbuddy.UpdateOperations.Companion.updatePathInfo

class UploadDrawingInfo {
    companion object{
        //For CustomPath
        var pathStringList : ArrayList<String> = ArrayList()

        private fun addPathToStringList(pathList: ArrayList<CustomPath>){
            pathStringList.clear()
            for (path in pathList){

                pathStringList.add(convertPathToString(path))
            }
        }

        fun updatePathInfoToFirebase(pathList: ArrayList<CustomPath>){
            if (pathList.isEmpty())
                pathStringList.clear()
            else{
                addPathToStringList(pathList)
                updatePathInfo(pathStringList)
            }
        }


        //For Brush (Paint)
        var brushStringList : ArrayList<String> = ArrayList()

        fun addBrushToStringList(brushList: ArrayList<CustomPaint>){
            brushStringList.clear()
            for(brush in brushList){
                brushStringList.add(convertBrushToString(brush))
            }
        }

        fun updateBrushINfoToFirebase(brushList: ArrayList<CustomPaint>){
            if (brushList.isEmpty())
                brushStringList.clear()
            else{
                addBrushToStringList(brushList)
                updateBrushInfo(brushStringList)
            }
        }

    }
}