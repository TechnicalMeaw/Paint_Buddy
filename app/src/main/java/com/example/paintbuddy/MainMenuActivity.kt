package com.example.paintbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.paintbuddy.constants.DatabaseLocations.Companion.USERINFO_LOCATION
import com.example.paintbuddy.constants.IntentStrings.Companion.NEW_DRAW_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main_menu.*
import java.util.*

class MainMenuActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        viewCanvasBtn.setOnClickListener {
            val intent = Intent(this, ViewDrawingInCanvas::class.java)
            startActivity(intent)
        }

        fab.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(NEW_DRAW_ID, "NEW")
            startActivity(intent)
        }

        drawingRecyclerView.adapter = this.adapter
    }
}