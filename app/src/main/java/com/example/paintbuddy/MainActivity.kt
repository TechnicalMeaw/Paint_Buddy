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
import androidx.core.content.ContextCompat
import com.example.paintbuddy.CanvasView.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var paint: CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paint = CanvasView(this)
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)

        alphaSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
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

        sizeSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
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
            }
            R.id.saveBtn -> {
                saveToGallery(this, getBitmapFromView(canvas), "Paint Buddy")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun Black(view: View) {
        changeBrush(true)

        CanvasView.currentColor = Color.BLACK
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        erase = false
    }
    fun Blue(view: View) {
        changeBrush(true)

        CanvasView.currentColor = Color.parseColor("#0099CC")
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        erase = false
    }
    fun Green(view: View) {
        changeBrush(true)

        CanvasView.currentColor = Color.parseColor("#669900")
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
        erase = false
    }
    fun Red(view: View) {
        changeBrush(true)

        CanvasView.currentColor = Color.parseColor("#FF4444")
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
        erase = false
    }
    fun Purple(view: View) {
        changeBrush(true)

        CanvasView.currentColor = Color.parseColor("#AA66CC")
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple)
        erase = false
    }
    fun Yellow(view: View) {
        changeBrush(true)

        CanvasView.currentColor = Color.parseColor("#FFBB33")
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.yellow)
        erase = false
    }

    fun erase(view: View) {
        changeBrush(true)

        currentColor = Color.parseColor("#FAFAFA")
        curColor.backgroundTintList = ContextCompat.getColorStateList(this, R.color.eraser)
        erase = true
    }

    fun configBrush(view: View) {
        if (sliderWindow.visibility == View.GONE)
            sliderWindow.visibility = View.VISIBLE
        else {
            sliderWindow.visibility = View.GONE

            if (currentAlpha != alphaSlider.progress) {
                changeBrush(true)
                currentAlpha = alphaSlider.progress
            }
        }
    }


    private fun getBitmapFromView(view: CanvasView): Bitmap{
        val bitmap = createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE);
        view.draw(canvas)
        return bitmap
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
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
}