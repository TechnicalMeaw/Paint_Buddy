package com.example.paintbuddy.conversion;

import com.example.paintbuddy.customClasses.CustomPaint;
import com.example.paintbuddy.customClasses.CustomPath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PathConversions {
    public static byte[] encodeToByteArray(CustomPath customPath) throws IOException{

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(customPath);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    public static CustomPath decodeToCustomPath(byte[] data) throws IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        CustomPath recover =(CustomPath) in.readObject();
        in.close();
        return recover;
    }

    public static byte[] encodeToByteArray(CustomPaint paint) throws IOException{

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(paint);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }


    public static CustomPaint decodeToCustomPaint(byte[] data) throws IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        CustomPaint recover =(CustomPaint) in.readObject();
        in.close();
        return recover;
    }

}
