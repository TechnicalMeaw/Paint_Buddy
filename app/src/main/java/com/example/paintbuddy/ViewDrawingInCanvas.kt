package com.example.paintbuddy

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.paintbuddy.StringConversions.Companion.convertStringToBrush
import com.example.paintbuddy.StringConversions.Companion.convertStringToPath
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_view_drawing_in_canvas.*
import java.lang.Exception

class ViewDrawingInCanvas : AppCompatActivity() {
    private val pathRef = FirebaseDatabase.getInstance().getReference("/pathInfo/")
    private val brushRef = FirebaseDatabase.getInstance().getReference("/brushInfo/")
    var pathStringList : ArrayList<String> = ArrayList()
    var brushStringList : ArrayList<String> = ArrayList()
    var customPathList : ArrayList<CustomPath> = ArrayList()
    var customBrushList : ArrayList<CustomPaint> = ArrayList()
    var brush = CustomPaint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drawing_in_canvas)

        updateCanvas(viewDrawingCanvas)

        brush.isAntiAlias = true
        brush.style = Paint.Style.STROKE
        brush.strokeJoin = Paint.Join.ROUND
        brush.strokeWidth = CanvasView.currentStroke
        brush.color = CanvasView.currentColor
        brush.alpha = CanvasView.currentAlpha

    }

    fun updateCanvas(view: ImageView){
        pathRef.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                pathStringList.add(snapshot.getValue() as String)
                convertToPathList(pathStringList, view)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                pathStringList.clear()
                pathStringList.add(snapshot.getValue() as String)
                convertToPathList(pathStringList, view)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                println("Child Removed")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })


        brushRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                brushStringList.add(snapshot.getValue() as String)
                convertToBrushList(brushStringList, view)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                brushStringList.clear()
                brushStringList.add(snapshot.getValue() as String)
                convertToBrushList(brushStringList, view)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                println("Child Removed")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun convertToPathList(list: ArrayList<String>, view: ImageView){
        customPathList.clear()
        for(l in list){
            customPathList.add(convertStringToPath(l))
        }
        drawToCanvas(view)
    }

    fun convertToBrushList(list: ArrayList<String>, view: ImageView){
        customBrushList.clear()
        for (l in list){
            customBrushList.add(convertStringToBrush(l))
        }
        drawToCanvas(view)
    }

    fun drawToCanvas(view: ImageView){
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        try {
            if (customPathList.isNotEmpty() && customPathList.size == customBrushList.size){
                customPathList.zip(customBrushList).forEach { pair ->
                    canvas.drawPath(pair.first, brush)
                }

                view.draw(canvas)
                view.setImageBitmap(bitmap)
            }
        }catch (e: Exception){
            println(e)
        }

    }


}