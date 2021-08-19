package com.example.paintbuddy.local

import android.R
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*


class LocalStorage {

    companion object {
        var sharedPref: SharedPreferences? = null

        var status: String?
            get() {
                return sharedPref?.getString("status", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("status", value)?.apply()
            }



        private fun saveToInternalStorage(context: Context, bitmapImage: Bitmap, filename: String): String? {
            val cw = ContextWrapper(context)
            // path to /data/data/your_app/app_data/imageDir
            val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val myPath = File(directory, "$filename.jpg")
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(myPath)
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return directory.absolutePath
        }




        private fun loadImageFromStorage(path: String, filename: String): Bitmap? {
            try {
                val f = File(path, "$filename.jpg")
                val b = BitmapFactory.decodeStream(FileInputStream(f))
                return b
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return null
            }
        }
    }
}