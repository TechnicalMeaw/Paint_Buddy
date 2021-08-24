package com.example.paintbuddy

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
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
import com.example.paintbuddy.constants.IntentStrings.Companion.NEW_DRAW_ID
import com.example.paintbuddy.local.DeleteCache.deleteCache
//import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.bgColor
import com.example.paintbuddy.updateDrawing.UpdateOperations.Companion.updateScreenResolution
import com.example.paintbuddy.updateDrawing.UploadDrawingInfo
//import com.example.paintbuddy.updateDrawing.UploadDrawingInfo.Companion.BgColor
//import com.example.paintbuddy.updateDrawing.UploadDrawingInfo.Companion.addDrawInfoToFirebase
//import com.example.paintbuddy.updateDrawing.UploadDrawingInfo.Companion.color
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    private var drawingId = ""
    var bgColor = R.color.eraser
    private lateinit var updator: UploadDrawingInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updator = UploadDrawingInfo()

        if (intent.getStringExtra(NEW_DRAW_ID).toString() == "NEW"){
            drawingId = UUID.randomUUID().toString()
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


//        canvas.setOnTouchListener { view, motionEvent ->
//
//        }

//        slider = findViewById<LinearLayout>(R.id.sliderWindow)
//        bgColor = findViewById(R.id.backgroundColorPane)

        if (FirebaseAuth.getInstance().uid != null){
            update(drawingId)
        }
    }


    var timer = Timer()
    private fun update(location: String){
        var pl = canvas.pathList.size
        var bgColor = canvas.backgroundColor
        timer.scheduleAtFixedRate(
            timerTask {

                if ((canvas.pathList.size != pl || canvas.backgroundColor != bgColor) && flag == false){

                    try {
//                        if (backgroundColor != bgColor)
//                            backgroundColorPane.visibility = View.GONE

//                        if (pathList.size != pl)
//                            sliderWindow.visibility = View.GONE

                        updator.addDrawInfoToFirebase(canvas.pathList, canvas.currentStroke, canvas.currentAlpha, location)
                        updateScreenResolution(canvas.width, canvas.height)
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
                updator.addDrawInfoToFirebase(canvas.pathList, canvas.currentStroke, canvas.currentAlpha)
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

        updator.color = "#000000"
        canvas.currentColor = Color.BLACK
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        canvas.erase = false
    }
    fun Blue(view: View) {
        canvas.changeBrush(true)

        updator.color = "#0099CC"
        canvas.currentColor = Color.parseColor(updator.color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        canvas.erase = false
    }
    fun Green(view: View) {
        canvas.changeBrush(true)

        updator.color = "#669900"
        canvas.currentColor = Color.parseColor(updator.color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
        canvas.erase = false
    }
    fun Red(view: View) {
        canvas.changeBrush(true)

        updator.color = "#FF4444"
        canvas.currentColor = Color.parseColor(updator.color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
        canvas.erase = false
    }
    fun Purple(view: View) {
        canvas.changeBrush(true)

        updator.color = "#AA66CC"
        canvas.currentColor = Color.parseColor(updator.color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple)
        canvas.erase = false
    }
    fun Yellow(view: View) {
        canvas.changeBrush(true)

        updator.color = "#FFBB33"
        canvas.currentColor = Color.parseColor(updator.color)
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow)
        canvas.erase = false
    }

    fun erase(view: View) {
        canvas.changeBrush(true)

        updator.color = "#FFFFFF"
        canvas.currentColor = Color.parseColor(updator.color)
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
        updator.BgColor = "#000000"
        canvas.backgroundColor = Color.BLACK
        changeBgColor(R.color.black)
    }
    fun BgBlue(view: View) {
        updator.BgColor = "#0099CC"
        canvas.backgroundColor = Color.parseColor(updator.BgColor)
        changeBgColor(R.color.blue)
    }
    fun BgGreen(view: View) {
        updator.BgColor = "#669900"
        canvas.backgroundColor = Color.parseColor(updator.BgColor)
        changeBgColor(R.color.green)
    }
    fun BgRed(view: View) {
        updator.BgColor = "#FF4444"
        canvas.backgroundColor = Color.parseColor(updator.BgColor)
        changeBgColor(R.color.red)
    }
    fun BgPurple(view: View) {
        updator.BgColor = "#AA66CC"
        canvas.backgroundColor = Color.parseColor(updator.BgColor)
        changeBgColor(R.color.purple)
    }
    fun BgYellow(view: View) {
        updator.BgColor = "#FFBB33"
        canvas.backgroundColor = Color.parseColor(updator.BgColor)
        changeBgColor(R.color.yellow)
    }

    fun BgWhite(view: View) {
        updator.BgColor = "#FFFFFF"
        canvas.backgroundColor = Color.WHITE
        changeBgColor(R.color.eraser)
    }

    fun BgGray(view: View) {
        updator.BgColor = "#AAAAAA"
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
//        val mActivityManager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
//        mActivityManager.killBackgroundProcesses("com.example.paintbuddy")
        deleteCache(this)
        this.cacheDir.deleteRecursively()
        timer.cancel()
        timer.purge();

        super.onDestroy()
    }

}