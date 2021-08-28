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

    var drawMap : HashMap<String, CustomPath> = HashMap()
    var brushColorMap : HashMap<String, String> = HashMap()
    var strokeMap : HashMap<String, Float> = HashMap()
    var alphaMap : HashMap<String, Int> = HashMap()

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


                        drawMap[snapshot.key.toString()] = convertStringToPath(path)
                        brushColorMap[snapshot.key.toString()] = brushColor
                        strokeMap[snapshot.key.toString()] = brushStroke.toFloat()
                        alphaMap[snapshot.key.toString()] = brushAlpha.toInt()
                    }catch (e: Exception){
                        drawMap.clear()
                        brushColorMap.clear()
                        strokeMap.clear()
                        alphaMap.clear()
                        println(e)
                    }
                    updateCanvas(drawMap, brushColorMap, view)
                }else{
                    Toast.makeText(this@ViewDrawingInCanvas, "Link Expired", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.value != null){
                    getScreenRes(viewDrawingCanvas)
                    try {
                        val path = snapshot.child("drawPath").value as String
                        bgColor = snapshot.child("bgColor").value as String

                        drawMap[snapshot.key.toString()] = convertStringToPath(path)
                    }catch (e: Exception){
                        drawMap.clear()
                        brushColorMap.clear()
                        strokeMap.clear()
                        alphaMap.clear()
                        println(e)
                    }
                }else{
                    drawMap.clear()
                    brushColorMap.clear()
                    strokeMap.clear()
                    alphaMap.clear()
                }

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


    private fun getScreenRes(view: ImageView){
        scrRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val w = snapshot.child("width").value as Long
                    val h = snapshot.child("height").value as Long
                    width = w.toInt()
                    height = h.toInt()
                    updateCanvas(drawMap, brushColorMap, view)

                }catch (e: Exception){
                    Toast.makeText(this@ViewDrawingInCanvas, "Invalid Link", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

   fun updateCanvas(pathMap: HashMap<String, CustomPath>, colorMap: HashMap<String, String>, view: ImageView){
       if (pathMap.size == colorMap.size)
           drawToCanvas(pathMap, colorMap, view)
   }

    private fun drawToCanvas(pathMap: HashMap<String, CustomPath>, colorMap: HashMap<String, String>, view: ImageView){
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        try {
            if (pathMap.isNotEmpty()){
                pathMap.toSortedMap().forEach { it ->

                    val brush = brushInit(colorMap[it.key]!!, strokeMap[it.key]!!, alphaMap[it.key.toString()]!!)
                    canvas.drawPath(it.value, brush)
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