package com.example.paintbuddy

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.paintbuddy.StringConversions.Companion.convertStringToBrush
import com.example.paintbuddy.StringConversions.Companion.convertStringToPath
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_drawing_in_canvas.*
import java.lang.Exception

class ViewDrawingInCanvas : AppCompatActivity() {
    private val drawRef = FirebaseDatabase.getInstance().getReference("/DrawInfo/")
    private val scrRef = FirebaseDatabase.getInstance().getReference("/ScreenRes/")
    var width = 1980
    var height = 1080

    var bgColor = "FFFFFF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drawing_in_canvas)

        getScreenRes()
        updateCanvas(viewDrawingCanvas)

    }

    private fun brushInit(color: String, stroke: Float, alpha: Int): CustomPaint {
        val brush = CustomPaint()
        brush.isAntiAlias = true
        brush.style = Paint.Style.STROKE
        brush.strokeJoin = Paint.Join.ROUND
        brush.strokeWidth = stroke
        brush.color = Color.parseColor(color)
        brush.alpha = alpha
        return brush
    }

    var drawMap : HashMap<String, CustomPath> = HashMap()
    var brushColorMap : HashMap<String, String> = HashMap()
    var strokeMap : HashMap<String, Float> = HashMap()
    var alphaMap : HashMap<String, Int> = HashMap()

    private fun updateCanvas(view: ImageView){
        getScreenRes()
        drawRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val path = snapshot.child("drawPath").value as String
                val brushColor = snapshot.child("brushColor").value as String
                val brushStroke = snapshot.child("brushStroke").value as Long
                val brushAlpha = snapshot.child("brushAlpha").value as Long
                bgColor = snapshot.child("bgColor").value as String


                drawMap[snapshot.key.toString()] = convertStringToPath(path)
                brushColorMap[snapshot.key.toString()] = brushColor
                strokeMap[snapshot.key.toString()] = brushStroke.toFloat()
                alphaMap[snapshot.key.toString()] = brushAlpha.toInt()

                updateCanvas(drawMap, brushColorMap, view)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val path = snapshot.child("drawPath").value as String
                bgColor = snapshot.child("bgColor").value as String

                drawMap[snapshot.key.toString()] = convertStringToPath(path)

                updateCanvas(drawMap, brushColorMap, view)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                drawMap.remove(snapshot.key.toString())
                brushColorMap.remove(snapshot.key.toString())
                strokeMap.remove(snapshot.key.toString())
                alphaMap.remove(snapshot.key.toString())

                println("Child Removed")

                updateCanvas(drawMap, brushColorMap, view)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun getScreenRes(){
        scrRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val w = snapshot.child("width").value as Long
                val h = snapshot.child("height").value as Long
                width = w.toInt()
                height = h.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

   fun updateCanvas(pathMap: HashMap<String, CustomPath>, colorMap: HashMap<String, String>, view: ImageView){
       if (pathMap.isNotEmpty() && pathMap.size == colorMap.size)
           drawToCanvas(pathMap, colorMap, view)
   }

    private fun drawToCanvas(pathMap: HashMap<String, CustomPath>, colorMap: HashMap<String, String>, view: ImageView){
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val s = pathMap.size

        try {
            pathMap.toSortedMap().forEach { it ->

                val brush = brushInit(colorMap[it.key]!!, strokeMap[it.key]!!, alphaMap[it.key.toString()]!!)
                canvas.drawPath(it.value, brush)

            }


//            bgColorView.setColorFilter(Color.parseColor(bgColor))
            bgColorView.setBackgroundColor(Color.parseColor(bgColor))
//            view.draw(canvas)

            view.setImageBitmap(bitmap)

        }catch (e: Exception){
            println(e)
        }

    }


}