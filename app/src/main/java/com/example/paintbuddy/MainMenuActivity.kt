package com.example.paintbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.paintbuddy.constants.DatabaseLocations.Companion.SAVED_DRAWINGS

import com.example.paintbuddy.constants.IntentStrings.Companion.NEW_DRAW_ID
import com.example.paintbuddy.customClasses.recyclerView.DrawListener
import com.example.paintbuddy.customClasses.recyclerView.DrawViewRVAdapter
import com.example.paintbuddy.firebaseClasses.SavedItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlin.collections.HashMap

class MainMenuActivity : AppCompatActivity(), DrawListener {

    private val hashMap = HashMap<String, SavedItem>()
    private lateinit var adapter : DrawViewRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        fab.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(NEW_DRAW_ID, "NEW")
            startActivity(intent)
        }

        adapter = DrawViewRVAdapter(this, this)
        drawingRecyclerView.layoutManager = GridLayoutManager(this, 2)
        drawingRecyclerView.adapter = this.adapter


        getSavedDrawings()
    }

    override fun onDrawItemClicked(drawItem: SavedItem) {
        Toast.makeText(this, drawItem.drawId, Toast.LENGTH_SHORT).show()
    }


    private fun getSavedDrawings(){
        val ref = FirebaseDatabase.getInstance().getReference("$SAVED_DRAWINGS/${FirebaseAuth.getInstance().uid}/")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val item = snapshot.getValue(SavedItem ::class.java)
                    hashMap[snapshot.key.toString()] = item!!
                    adapter.updateList(hashMap.values.toList().sortedBy { it.lastModified })
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val item = snapshot.value as SavedItem
                    hashMap[snapshot.key.toString()] = item
                    adapter.updateList(hashMap.values.toList().sortedBy { it.lastModified })
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    hashMap.remove(snapshot.key.toString())
                    adapter.updateList(hashMap.values.toList().sortedBy { it.lastModified })
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}