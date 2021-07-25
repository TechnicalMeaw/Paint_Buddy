package com.example.paintbuddy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

public class CanvasView extends View {

    public ViewGroup.LayoutParams params;
    public static ArrayList<CustomPath> pathList = new ArrayList<>();
    public static ArrayList<CustomPaint> brushList = new ArrayList<>();
    public static CustomPath path = new CustomPath();
    public static CustomPaint brush = new CustomPaint();
    public static int currentColor = Color.BLACK;
    static int currentAlpha = 255;
    static float currentStroke = 5f;
    public static Boolean erase = false;
    public static int backgroundColor = Color.WHITE;

    //For redo buffer
    public static ArrayList<CustomPath> pathBufferList = new ArrayList<>();
    public static ArrayList<CustomPaint> brushBufferList = new ArrayList<>();

//    private List<String> stringPaths = new ArrayList<String>();


    public CanvasView(Context context) {
        super(context);
        this.setDrawingCacheEnabled(true);
        init();

    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setDrawingCacheEnabled(true);
        init();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setDrawingCacheEnabled(true);
        init();
    }

    public void init(){

        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(currentStroke);
        brush.setColor(currentColor);
        brush.setAlpha(currentAlpha);
        invalidate();
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    static Boolean flag = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float posX = event.getX();
        float posY = event.getY();
        init();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                flag = true;
                pathBufferList.clear();
                brushBufferList.clear();
                path.moveTo(posX, posY);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                flag = true;
                path.lineTo(posX, posY);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                flag = false;
                if (erase){
//                    brush.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                    brush.setXfermode(null);

                }
                changeBrush(false);
                return true;
            default:
                return false;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

//        canvas.drawColor(backgroundColor);

        Iterator<CustomPath> it1 = pathList.iterator();
        Iterator<CustomPaint> it2 = brushList.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            try {
                canvas.drawPath(it1.next(), it2.next());
            }catch (Exception e){
                Log.e("CanvasView", "Error! ::" + e);
            }

//            canvas.setBitmap(bitmap);
//            invalidate();
        }

        if (flag)
            canvas.drawPath(path, brush);

        // Convert Path List to String List
//        stringPaths.clear();
//        for(Path path : pathList)
//        {
//            stringPaths.add(String.valueOf(path));
//        }

//        for (String string : stringPaths){
//            pathList.add()
//        }
    }

    public static void changeBrush(Boolean changedBrush){
        if (!changedBrush){
            pathList.add(path);
            brushList.add(brush);
        }
        brush = new CustomPaint();
        path = new CustomPath();
    }


    public static void undo(){
        if(!pathList.isEmpty() || !brushList.isEmpty()){

            pathBufferList.add(pathList.remove(pathList.size() -1));
            brushBufferList.add(brushList.remove(brushList.size() -1));

        }
    }

    public static void redo(){
        if (!pathBufferList.isEmpty() || !brushBufferList.isEmpty()){

            pathList.add(pathBufferList.remove(pathBufferList.size()-1));
            brushList.add(brushBufferList.remove(brushBufferList.size() - 1));

        }
    }

    public static void clear(){
        pathList.clear();
        brushList.clear();
    }



}
