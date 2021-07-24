package com.example.paintbuddy

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import com.example.paintbuddy.CanvasView.*

class ImageItem(val bitmapUri: String, val BackgroundColor: Int) {
    constructor() : this("", -1)
}

