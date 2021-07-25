package com.example.paintbuddy

import android.content.ContentValues
import android.content.Context
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
import com.example.paintbuddy.CanvasView.*
import com.example.paintbuddy.UpdateOperations.Companion.bgColor
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {

    private lateinit var paint: CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paint = CanvasView(this)


        alphaSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentAlpha = progress
                alphaSliderText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("MainActivity", "SeekBar Touched")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                changeBrush(true)
            }
        })

        sizeSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentStrock = progress.toFloat()
                sizeSliderText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("MainActivity", "SeekBar Touched")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                changeBrush(true)
            }
        })


        undoToolBtn.setOnClickListener {
            undo()
            canvas.invalidate()
        }
        redoToolBtn.setOnClickListener{
            redo()
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
        backgroundToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        backgroundExtraSpace.background = AppCompatResources.getDrawable(applicationContext, bgColor)


//        slider = findViewById<LinearLayout>(R.id.sliderWindow)
//        bgColor = findViewById(R.id.backgroundColorPane)
        update()
    }


    private fun update(){
        var pl = pathList.size
        var bgColor = backgroundColor
        Timer().scheduleAtFixedRate(
            timerTask {
            if ((pathList.size != pl || backgroundColor != bgColor) && flag == false){

                UploadBitmap.uploadImageToFirebase(getBitmapFromView(canvas, false))
                Log.d("MainActivity", "Updated Drawing :: Success")
                pl = pathList.size
                bgColor = backgroundColor
            }
        }, 2000, 900)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when( item.itemId){
            R.id.undoBtn -> {
                undo()
                canvas.invalidate()
            }
            R.id.redoBtn -> {
                redo()
                canvas.invalidate()
            }
            R.id.clearBtn -> {
                clear()
                canvas.invalidate()
            }
            R.id.saveBtn -> {
                saveToGallery(this, getBitmapFromView(canvas, true), "Paint Buddy")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun Black(view: View) {
        changeBrush(true)

        currentColor = Color.BLACK
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        erase = false
    }
    fun Blue(view: View) {
        changeBrush(true)

        currentColor = Color.parseColor("#0099CC")
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        erase = false
    }
    fun Green(view: View) {
        changeBrush(true)

        currentColor = Color.parseColor("#669900")
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
        erase = false
    }
    fun Red(view: View) {
        changeBrush(true)

        currentColor = Color.parseColor("#FF4444")
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
        erase = false
    }
    fun Purple(view: View) {
        changeBrush(true)

        currentColor = Color.parseColor("#AA66CC")
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple)
        erase = false
    }
    fun Yellow(view: View) {
        changeBrush(true)

        currentColor = Color.parseColor("#FFBB33")
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow)
        erase = false
    }

    fun erase(view: View) {
        changeBrush(true)

        currentColor = Color.parseColor("#FFFFFF")
        brushToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.eraser)
        erase = true
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
        val canvas = Canvas(bitmap)
        if (isSaving)
            canvas.drawColor(backgroundColor)
        view.draw(canvas)
        return bitmap
    }

    private fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
        val filename = "${System.currentTimeMillis()}.png"
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
        backgroundColor = Color.BLACK
        changeBgColor(R.color.black)
    }
    fun BgBlue(view: View) {
        backgroundColor = Color.parseColor("#0099CC")
        changeBgColor(R.color.blue)
    }
    fun BgGreen(view: View) {
        backgroundColor = Color.parseColor("#669900")
        changeBgColor(R.color.green)
    }
    fun BgRed(view: View) {
        backgroundColor = Color.parseColor("#FF4444")
        changeBgColor(R.color.red)
    }
    fun BgPurple(view: View) {
        backgroundColor = Color.parseColor("#AA66CC")
        changeBgColor(R.color.purple)
    }
    fun BgYellow(view: View) {
        backgroundColor = Color.parseColor("#FFBB33")
        changeBgColor(R.color.yellow)
    }

    fun BgWhite(view: View) {
        backgroundColor = Color.WHITE
        changeBgColor(R.color.eraser)
    }

    fun BgGray(view: View) {
        backgroundColor = Color.GRAY
        changeBgColor(R.color.gray)

        println(R.color.gray.toString())
    }

    private fun changeBgColor(color: Int){
        bgColor = color
        backgroundToolBtn.backgroundTintList = ContextCompat.getColorStateList(this, color)
        backgroundExtraSpace.background = AppCompatResources.getDrawable(applicationContext, color)
    }
}