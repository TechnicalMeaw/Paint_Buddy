package com.example.paintbuddy

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paintbuddy.constants.DatabaseLocations
import com.example.paintbuddy.constants.DatabaseLocations.Companion.DRAWING_LOCATION
import com.example.paintbuddy.constants.DatabaseLocations.Companion.SCREEN_RES_LOCATION
import com.example.paintbuddy.constants.IntentStrings.Companion.SAVED_DRAWING_LOCATION
import com.example.paintbuddy.conversion.StringConversions.Companion.convertStringToPath
import com.example.paintbuddy.customClasses.CustomPaint
import com.example.paintbuddy.customClasses.CustomPath
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_drawing_in_canvas.*


class ViewDrawingInCanvas : AppCompatActivity() {
    private var drawRef = FirebaseDatabase.getInstance().getReference("${DRAWING_LOCATION}/${FirebaseAuth.getInstance().uid}/")
    private var scrRef = FirebaseDatabase.getInstance().getReference("$SCREEN_RES_LOCATION/${FirebaseAuth.getInstance().uid}/")
    var width = 1080
    var height = 1980
    var bgColor = "#FFFFFF"
    var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drawing_in_canvas)


        val data: Uri? = intent.data
        location = if (data!= null && data.toString().length > 75){
            data.toString().substring(31)
        }else {
            intent.getStringExtra(SAVED_DRAWING_LOCATION).toString()
        }

        drawRef = FirebaseDatabase.getInstance().getReference("${DRAWING_LOCATION}/${location}/")
        scrRef = FirebaseDatabase.getInstance().getReference("$SCREEN_RES_LOCATION/${location.substring(0,28)}/")
        Log.d("ViewDraw", "uri: $data, location: $location")


        getScreenRes(viewDrawingCanvas)

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

    var drawMap : HashMap<Int, CustomPath> = HashMap()
    var brushMap: HashMap<Int, Paint> = HashMap()

    private fun updateCanvas(view: ImageView){
        drawRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    getScreenRes(viewDrawingCanvas)
                    try {
                        val path = snapshot.child("drawPath").value as String
                        val brushColor = snapshot.child("brushColor").value as String
                        val brushStroke = snapshot.child("brushStroke").value as Long
                        val brushAlpha = snapshot.child("brushAlpha").value as Long
                        bgColor = snapshot.child("bgColor").value as String


                        drawMap[snapshot.key!!.toInt()] = convertStringToPath(path)
                        brushMap[snapshot.key!!.toInt()] = brushInit(brushColor, brushStroke.toFloat(), brushAlpha.toInt())

                    }catch (e: Exception){
                        drawMap.clear()
                        brushMap.clear()
                        println(e)
                    }
                    updateCanvas(drawMap, brushMap, view)
                }else{
                    Toast.makeText(this@ViewDrawingInCanvas, "Link Expired", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.value != null){
                    getScreenRes(viewDrawingCanvas)
                    try {
                        val path = snapshot.child("drawPath").value as String
                        val brushColor = snapshot.child("brushColor").value as String
                        val brushStroke = snapshot.child("brushStroke").value as Long
                        val brushAlpha = snapshot.child("brushAlpha").value as Long
                        bgColor = snapshot.child("bgColor").value as String

                        drawMap[snapshot.key!!.toInt()] = convertStringToPath(path)
                        brushMap[snapshot.key!!.toInt()] = brushInit(brushColor, brushStroke.toFloat(), brushAlpha.toInt())
                    }catch (e: Exception){
                        drawMap.clear()
                        brushMap.clear()
                        println(e)
                    }
                }else{
                    drawMap.clear()
                    brushMap.clear()
                }

                updateCanvas(drawMap, brushMap, view)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                drawMap.remove(snapshot.key!!.toInt())
                brushMap.remove(snapshot.key!!.toInt())

                println("Child Removed")

                updateCanvas(drawMap, brushMap, view)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun getScreenRes(view: ImageView){
        scrRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val w = snapshot.child("width").value as Long
                    val h = snapshot.child("height").value as Long
                    width = w.toInt()
                    height = h.toInt()
                    updateCanvas(drawMap, brushMap, view)

                }catch (e: Exception){
                    Toast.makeText(this@ViewDrawingInCanvas, "Invalid Link", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

   fun updateCanvas(pathMap: HashMap<Int, CustomPath>, brushMap: HashMap<Int, Paint>, view: ImageView){
       if (pathMap.size == brushMap.size)
           drawToCanvas(pathMap, brushMap, view)
   }

    private fun drawToCanvas(pathMap: HashMap<Int, CustomPath>, brushMap: HashMap<Int, Paint>, view: ImageView){
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        try {
            if (pathMap.isNotEmpty()){
                pathMap.toSortedMap().forEach { it ->

                    brushMap[it.key]?.let { brush -> canvas.drawPath(it.value, brush) }
                }
            }

            bgColorView.setBackgroundColor(Color.parseColor(bgColor))
//            view.draw(canvas)

            view.setImageBitmap(bitmap)

        }catch (e: Exception){
            println(e)
        }

    }


}