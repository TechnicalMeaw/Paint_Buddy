package com.example.paintbuddy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.paintbuddy.constants.DatabaseLocations
import com.example.paintbuddy.local.LocalStorage
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_flash_screen.*

class FlashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_screen)

        // App Check
        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        LocalStorage.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        checkStatus()
    }

    private fun checkStatus(){
        when {
            FirebaseAuth.getInstance().uid == null -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            LocalStorage.status == "registered" -> {
                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                checkRegistered()
            }
        }
    }

    private fun checkRegistered(){
        val ref = FirebaseDatabase.getInstance().getReference("${DatabaseLocations.USERINFO_LOCATION}/${FirebaseAuth.getInstance().uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("firstName")) {
                    LocalStorage.status = "registered"
                    val intent = Intent(this@FlashScreenActivity, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    val intent = Intent(this@FlashScreenActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}