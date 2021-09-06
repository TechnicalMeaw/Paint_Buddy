package com.example.paintbuddy.local

import android.app.Application
import com.example.paintbuddy.constants.DatabaseLocations.Companion.SAVED_DRAWINGS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class FirebaseOfflineDatabase: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}