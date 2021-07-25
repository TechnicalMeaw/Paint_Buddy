package com.example.paintbuddy;

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
        byte[] array = outputStream.toByteArray();
        return array;
    }

    public static CustomPath decodeToCustomPath(byte[] data) throws IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        CustomPath recover =(CustomPath) in.readObject();
        in.close();
        return recover;

    }

}
