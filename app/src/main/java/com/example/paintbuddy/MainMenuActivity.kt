package com.example.paintbuddy


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.GridLayoutManager
import com.example.paintbuddy.constants.DatabaseLocations.Companion.SAVED_DRAWINGS

import com.example.paintbuddy.constants.IntentStrings.Companion.NEW_DRAW_ID
import com.example.paintbuddy.constants.IntentStrings.Companion.SAVED_DRAWING_LOCATION
import com.example.paintbuddy.customClasses.recyclerView.DrawListener
import com.example.paintbuddy.customClasses.recyclerView.DrawViewRVAdapter
import com.example.paintbuddy.dialogBox.DeleteWarning.Companion.showDeleteWarningDialog
import com.example.paintbuddy.dialogBox.RenameBox.Companion.showRenameDialog
import com.example.paintbuddy.firebaseClasses.SavedItem
import com.example.paintbuddy.updateDrawing.UpdateSavedDrawings.Companion.deleteDrawing
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
            intent.putExtra(NEW_DRAW_ID, "NEW")
            startActivity(intent)
        }

        adapter = DrawViewRVAdapter(this, this)
        drawingRecyclerView.layoutManager = GridLayoutManager(this, 2)
        drawingRecyclerView.adapter = this.adapter


        if (FirebaseAuth.getInstance().uid != null){
            getSavedDrawings()
        }
    }

    override fun onDrawItemClicked(drawItem: SavedItem) {
        try {
            val intent = Intent(this, ViewDrawingInCanvas::class.java)
            intent.putExtra(SAVED_DRAWING_LOCATION, "${drawItem.userId}/${drawItem.drawId}")
            startActivity(intent)
        }catch (e:Exception){
            e.stackTrace
        }
    }

    override fun onSavedMenuViewBtnClicked(drawItem: SavedItem) {
        try {
            val intent = Intent(this, ViewDrawingInCanvas::class.java)
            intent.putExtra(SAVED_DRAWING_LOCATION, "${drawItem.userId}/${drawItem.drawId}")
            startActivity(intent)
        }catch (e:Exception){
            e.stackTrace
        }

    }

    override fun onSavedMenuEditBtnClicked(drawItem: SavedItem) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(NEW_DRAW_ID, "${drawItem.userId} ${drawItem.drawId} ${drawItem.thumbUri} ${drawItem.nodeCount}")
        startActivity(intent)
    }

    override fun onSavedMenuShareBtnClicked(drawItem: SavedItem) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://paintbuddy.com/drawing/${drawItem.userId}/${drawItem.drawId}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onSavedMenuRenameBtnClicked(drawItem: SavedItem) {
        showRenameDialog(this, drawItem)
    }

    override fun onSavedMenuDeleteBtnClicked(drawItem: SavedItem) {
        showDeleteWarningDialog(this, drawItem)
    }

    private fun getSavedDrawings(){
        val ref = FirebaseDatabase.getInstance().getReference("$SAVED_DRAWINGS/${FirebaseAuth.getInstance().uid}/")
        ref.keepSynced(true)

        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val item = snapshot.getValue(SavedItem ::class.java)
                    hashMap[snapshot.key.toString()] = item!!
                    adapter.updateList(hashMap.values.toList().sortedBy { it.lastModified }.reversed())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val item = snapshot.getValue(SavedItem ::class.java)
                    hashMap[snapshot.key.toString()] = item!!
                    adapter.updateList(hashMap.values.toList().sortedBy { it.lastModified }.reversed())
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    hashMap.remove(snapshot.key.toString())
                    adapter.updateList(hashMap.values.toList().sortedBy { it.lastModified }.reversed())
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