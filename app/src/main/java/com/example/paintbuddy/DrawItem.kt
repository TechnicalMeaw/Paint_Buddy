package com.example.paintbuddy

class DrawItem(val DrawPath: String, val BrushColor: String, val BrushStroke: Float, val BrushAlpha: Int, val BgColor: String) {

    constructor() : this("", "#000000", 5F, 255,"#FFFFFF")
}
