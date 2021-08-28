package com.example.paintbuddy

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.paintbuddy.CanvasView.flag
import com.example.paintbuddy.constants.DatabaseLocations
import com.example.paintbuddy.constants.DatabaseLocations.Companion.DRAWING_LOCATION
import com.example.paintbuddy.constants.IntentStrings.Companion.NEW_DRAW_ID
import com.example.paintbuddy.conversion.StringConversions
import com.example.paintbuddy.customClasses.CustomPaint
import com.example.paintbuddy.firebaseClasses.DrawItem
import com.example.paintbuddy.local.DeleteCache.deleteCache
import com.example.paintbuddy.updateDrawing.UpdateOperations
//import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.bgColor
//import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.updateScreenResolution
import com.example.paintbuddy.updateDrawing.UpdateSavedDrawings.Companion.saveDrawing
import com.example.paintbuddy.updateDrawing.UploadDrawingInfo
import com.example.paintbuddy.updateDrawing.UploadDrawingInfo.Companion.addDrawInfoToFirebase
import com.example.paintbuddy.updateDrawing.UploadDrawingInfo.Companion.BgColor
import com.example.paintbuddy.updateDrawing.UploadDrawingInfo.Companion.color
import com.example.paintbuddy.updateDrawing.UploadDrawingInfo.Companion.drawList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    private var drawingId = ""
    var bgColor = R.color.eraser
    private val updater = UpdateOperations()

    private var drawId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawId = intent.getStringExtra(NEW_DRAW_ID).toString()

        if (drawId == "NEW"){
            drawingId = UUID.randomUUID().toString()
            UploadDrawingInfo.init()
        }else{
            val credentials = drawId.split(" ")
            drawingId = credentials[1]
            Thread{
                retrieveSavedDrawing(credentials)
            }.start()

        }

        alphaSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                canvas.currentAlpha = progress
                alphaSliderText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("MainActivity", "SeekBar Touched")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                canvas.changeBrush(true)
            }
        })

        sizeSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                canvas.currentStroke = progress.toFloat()
                sizeSliderText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("MainActivity", "SeekBar Touched")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                canvas.changeBrush(true)
            }
        })

        undoToolBtn.setOnClickListener {
            canvas.undo()
            canvas.invalidate()
        }
        redoToolBtn.setOnClickListener{
            canvas.redo()
            canvas.invalidate()
        }

        backgroundToolBtn.setOnClickListener{
            when (backgroundColorPane.visibility) {
                View.VISIBLE -> backgroundColorPane.visibility = View.GONE
                else -> {
                    sliderWindow.visibility = View.GONE
                    backgroundColorPane.visibility = View.VISIBLE
                }
            }
        }
        backgroundToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.eraser)
        backgroundExtraSpace.background = AppCompatResources.getDrawable(applicationContext, bgColor)


        if (drawId == "NEW" && FirebaseAuth.getInstance().uid != null){
            update(drawingId)
        }
    }

    private fun retrieveSavedDrawing(list: List<String>) {
        val ref = FirebaseDatabase.getInstance().getReference("$DRAWING_LOCATION/${list[0]}/${list[1]}")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    try {
                        val item = snapshot.getValue(DrawItem::class.java)
                        UploadDrawingInfo.drawList.add(item!!)
                        val path = snapshot.child("drawPath").value as String
                        canvas.pathList.add(StringConversions.convertStringToPath(path))
                        canvas.brushList.add(brushInit(item.BrushColor, item.BrushStroke, item.BrushAlpha))
                        BgColor = item.BgColor
                        canvas.backgroundColor = Color.parseColor(BgColor)
                        canvas.invalidate()
                    }catch (e: Exception){
                        Log.e("MainActivity", "$e")
                    }

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                if (snapshot.exists()) {
//                    val item = snapshot.getValue(DrawItem::class.java)
//                    drawList[snapshot.key!!.toInt()] = item!!
//                    canvas.pathList[snapshot.key!!.toInt()] = StringConversions.convertStringToPath(item!!.DrawPath)
//                    canvas.brushList.add(brushInit(item.BrushColor, item.BrushStroke, item.BrushAlpha))
//                    BgColor = item.BgColor
//                    canvas.backgroundColor = Color.parseColor(BgColor)
//                }
                ref.removeEventListener(this)
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


    var timer = Timer()
    private fun update(location: String){
        var pl = canvas.pathList.size
        var bgColor = canvas.backgroundColor


        timer.scheduleAtFixedRate(
            timerTask {

                if ((canvas.pathList.size != pl || canvas.backgroundColor != bgColor) && flag == false){

                    try {

                        addDrawInfoToFirebase(canvas.pathList, canvas.currentStroke, canvas.currentAlpha, location)
                        updater.updateScreenResolution(canvas.width, canvas.height)
                        Log.d("MainActivity", "Updated Drawing :: Success")

                        pl = canvas.pathList.size
                        bgColor = canvas.backgroundColor

                    }catch (e : Exception){
                        Log.e("MainActivity","$e")
                    }

                }
            }, 100, 15
        )

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when( item.itemId){
            R.id.undoBtn -> {
                canvas.undo()
                canvas.invalidate()
            }
            R.id.redoBtn -> {
                canvas.redo()
                canvas.invalidate()
                addDrawInfoToFirebase(canvas.pathList, canvas.currentStroke, canvas.currentAlpha)
            }
            R.id.clearBtn -> {
                canvas.clear()
                canvas.invalidate()
            }
            R.id.saveBtn -> {
                saveToGallery(this, getBitmapFromView(canvas, true), "Paint Buddy")
            }
            R.id.shareBtn -> {
                if (FirebaseAuth.getInstance().uid != null){
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "https://paintbuddy.com/drawing/${FirebaseAuth.getInstance().uid}/$drawingId")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }else{
                    Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun Black(view: View) {
        canvas.changeBrush(true)

        color = "#000000"
        canvas.currentColor = Color.BLACK
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        canvas.erase = false
    }
    fun Blue(view: View) {
        canvas.changeBrush(true)

        color = "#0099CC"
        canvas.currentColor = Color.parseColor(color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        canvas.erase = false
    }
    fun Green(view: View) {
        canvas.changeBrush(true)

        color = "#669900"
        canvas.currentColor = Color.parseColor(color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
        canvas.erase = false
    }
    fun Red(view: View) {
        canvas.changeBrush(true)

        color = "#FF4444"
        canvas.currentColor = Color.parseColor(color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
        canvas.erase = false
    }
    fun Purple(view: View) {
        canvas.changeBrush(true)

        color = "#AA66CC"
        canvas.currentColor = Color.parseColor(color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple)
        canvas.erase = false
    }
    fun Yellow(view: View) {
        canvas.changeBrush(true)

        color = "#FFBB33"
        canvas.currentColor = Color.parseColor(color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow)
        canvas.erase = false
    }

    fun erase(view: View) {
        canvas.changeBrush(true)

        color = "#FFFFFF"
        canvas.currentColor = Color.parseColor(color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.eraser)
        canvas.erase = true
    }

    fun configBrush(view: View) {
        if (sliderWindow.visibility == View.GONE){
            backgroundColorPane.visibility = View.GONE
            sliderWindow.visibility = View.VISIBLE
        }
        else {
            sliderWindow.visibility = View.GONE
        }
    }


    private fun getBitmapFromView(view: CanvasView, isSaving: Boolean): Bitmap{
        val bitmap = createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvasImage = Canvas(bitmap)
        if (isSaving)
            canvasImage.drawColor(canvas.backgroundColor)
        view.draw(canvasImage)
        return bitmap
    }

    private fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
        val filename = "${drawingId}.png"
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DCIM}/$albumName"
                )
            }

            context.contentResolver.let {
                it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                    it.openOutputStream(uri)?.let(write)
                }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + albumName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, filename)
            write(FileOutputStream(image))
        }

        Handler().postDelayed({
            Toast.makeText(this, "Image saved at: DCIM/$albumName", Toast.LENGTH_LONG).show()
        }, 150)
    }

    fun BgBlack(view: View) {
        BgColor = "#000000"
        canvas.backgroundColor = Color.BLACK
        changeBgColor(R.color.black)
    }
    fun BgBlue(view: View) {
        BgColor = "#0099CC"
        canvas.backgroundColor = Color.parseColor(BgColor)
        changeBgColor(R.color.blue)
    }
    fun BgGreen(view: View) {
        BgColor = "#669900"
        canvas.backgroundColor = Color.parseColor(BgColor)
        changeBgColor(R.color.green)
    }
    fun BgRed(view: View) {
        BgColor = "#FF4444"
        canvas.backgroundColor = Color.parseColor(BgColor)
        changeBgColor(R.color.red)
    }
    fun BgPurple(view: View) {
        BgColor = "#AA66CC"
        canvas.backgroundColor = Color.parseColor(BgColor)
        changeBgColor(R.color.purple)
    }
    fun BgYellow(view: View) {
        BgColor = "#FFBB33"
        canvas.backgroundColor = Color.parseColor(BgColor)
        changeBgColor(R.color.yellow)
    }

    fun BgWhite(view: View) {
        BgColor = "#FFFFFF"
        canvas.backgroundColor = Color.WHITE
        changeBgColor(R.color.eraser)
    }

    fun BgGray(view: View) {
        BgColor = "#AAAAAA"
        canvas.backgroundColor = Color.GRAY
        changeBgColor(R.color.gray)

        println(R.color.gray.toString())
    }

    private fun changeBgColor(color: Int){
        bgColor = color
        backgroundToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, color)
        backgroundExtraSpace.background = AppCompatResources.getDrawable(applicationContext, color)
    }






    override fun onDestroy() {
        timer.cancel()
        timer.purge()

        if (drawId == "NEW" && canvas.pathList.size > 0){
            Thread{
                saveDrawing(drawingId, getBitmapFromView(canvas, true), "Untitled")
            }.start()
        }
        super.onDestroy()
    }


}