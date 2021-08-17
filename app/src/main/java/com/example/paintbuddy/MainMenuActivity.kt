package com.example.paintbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.paintbuddy.constants.DatabaseLocations.Companion.USERINFO_LOCATION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        if (FirebaseAuth.getInstance().uid != null)
//            checkRegistered()

        drawBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        viewCanvasBtn.setOnClickListener {
            val intent = Intent(this, ViewDrawingInCanvas::class.java)
            startActivity(intent)
        }
    }

//    private fun checkRegistered(){
//        val ref = FirebaseDatabase.getInstance().getReference("$USERINFO_LOCATION/${FirebaseAuth.getInstance().uid}")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                when {
//                    !snapshot.exists() -> {
//                        val intent = Intent(this@MainMenuActivity, LoginActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                    snapshot.hasChild("First Name") -> {
//                        TODO()
//                    }
//                    else -> {
//                        val intent = Intent(this@MainMenuActivity, RegisterUserActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }
}