package com.example.paintbuddy.dialogBox

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.paintbuddy.R
import com.example.paintbuddy.dialogBox.LoadingScreen.Companion.showLoadingDialog
import com.example.paintbuddy.firebaseClasses.DrawItem
import com.example.paintbuddy.firebaseClasses.SavedItem
import com.example.paintbuddy.updateDrawing.UpdateSavedDrawings
import kotlinx.android.synthetic.main.delete_warning_dialog.view.*

class DeleteWarning {

    companion object{
        fun showDeleteWarningDialog(context: Context, savedItem: SavedItem){
            val mDialogView: View = LayoutInflater.from(context).inflate(R.layout.delete_warning_dialog, null)

            val mBuilder = AlertDialog.Builder(context)
                .setTitle("Delete")
                .setView(mDialogView)
                .setCancelable(true)

            val mAlertDialog = mBuilder.show()

            mDialogView.deleteNegativeButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

            mDialogView.deletePositiveButton.setOnClickListener {
                mAlertDialog.dismiss()
                // Show Loading Dialog
                showLoadingDialog(context)

                UpdateSavedDrawings.deleteDrawing(
                    context,
                    savedItem.userId,
                    savedItem.drawId,
                    savedItem.thumbUri
                )
            }
        }
    }
}