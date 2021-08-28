package com.example.paintbuddy.dialogBox

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.paintbuddy.R
import com.example.paintbuddy.constants.DatabaseLocations
import com.example.paintbuddy.firebaseClasses.SavedItem
import com.example.paintbuddy.updateDrawing.UpdateSavedDrawings.Companion.updateSavedDrawingTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.rename_dialog_box.view.*


class RenameBox {
    companion object{

        fun showRenameDialog(context: Context, drawItem: SavedItem){

            val mDialogView: View = LayoutInflater.from(context).inflate(R.layout.rename_dialog_box, null)

            val mBuilder = AlertDialog.Builder(context)
                .setTitle("Rename")
                .setView(mDialogView)
                .setCancelable(true)

            val mAlertDialog = mBuilder.show()

            mDialogView.renameNegativeButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

            mDialogView.renamePositiveButton.setOnClickListener {
                val newTitle = mDialogView.renameEditText.text.toString()
                if (newTitle != ""){
                    updateSavedDrawingTitle(context, newTitle, drawItem)
                    mAlertDialog.dismiss()
                }else{
                    Toast.makeText(context, "Invalid Title", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}