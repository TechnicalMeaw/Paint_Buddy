package com.example.paintbuddy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_view_drawing.*


class ViewDrawingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drawing)


        getImageFromFirebase()

    }

    var imageItem: ImageItem? = null
    fun getImageFromFirebase(){
        val ref = FirebaseDatabase.getInstance().getReference("/paths/")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("bitmapUri")){
                    imageItem = snapshot.getValue(ImageItem::class.java)!!
                    Log.d("ViewDrawingActivity", "Data retrieved")
                    showImage()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ViewDrawingActivity", "Error!")
            }
        })
    }


    fun showImage(){

        val imageBytes = Compressor.decompress(Base64.decode(imageItem?.bitmapUri, Base64.DEFAULT))
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


        drawingImageView.setImageBitmap(decodedImage)
        try {
            drawingBackground.background = AppCompatResources.getDrawable(applicationContext, imageItem!!.BackgroundColor)
        }catch (e: Exception){
            Log.e("ViewDrawingActivity", "bgColor :: Error :: ${imageItem?.BackgroundColor}")
        }
        
    }
}