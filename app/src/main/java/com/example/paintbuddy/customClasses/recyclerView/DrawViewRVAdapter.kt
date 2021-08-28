package com.example.paintbuddy.customClasses.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.paintbuddy.R
import com.example.paintbuddy.conversion.TimeConversion.getDate
import com.example.paintbuddy.firebaseClasses.SavedItem



class DrawViewRVAdapter(val context: Context, private val listener: DrawListener) : RecyclerView.Adapter<DrawViewRVAdapter.DrawingViewHolder>() {

    private var allDrawings = ArrayList<SavedItem>()

    inner class DrawingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val thumb : ImageView = itemView.findViewById(R.id.drawThumbImageView)
        val title : TextView = itemView.findViewById(R.id.drawTitleView)
        val date : TextView = itemView.findViewById(R.id.modifiedDateView)
        val toolbar : Toolbar = itemView.findViewById(R.id.drawToolBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawingViewHolder {
        val viewHolder = DrawingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.saved_draw_item, parent, false))
        viewHolder.thumb.setOnClickListener {
            listener.onDrawItemClicked(allDrawings[viewHolder.bindingAdapterPosition])
        }
        viewHolder.toolbar.inflateMenu(R.menu.saved_item_menu)

        return viewHolder
    }

    override fun onBindViewHolder(holder: DrawingViewHolder, position: Int) {
        val currentDrawing = allDrawings[position]
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.brush)
            .error(R.drawable.brush)
        Glide.with(context).load(currentDrawing.thumbUri).apply(options).into(holder.thumb)

        holder.title.text = currentDrawing.title
        holder.date.text = getDate(currentDrawing.lastModified)
        holder.toolbar.setOnMenuItemClickListener( object : MenuItem.OnMenuItemClickListener,
            Toolbar.OnMenuItemClickListener {

            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item?.itemId){
                    R.id.viewSavedItemBtn -> {
                        listener.onSavedMenuViewBtnClicked(allDrawings[holder.bindingAdapterPosition])
                    }
                    R.id.editSavedItemBtn -> {
                        listener.onSavedMenuEditBtnClicked(allDrawings[holder.bindingAdapterPosition])
                    }
                    R.id.shareSavedItemBtn -> {
                        listener.onSavedMenuShareBtnClicked(allDrawings[holder.bindingAdapterPosition])
                    }
                    R.id.renameSavedItemBtn -> {
                        listener.onSavedMenuRenameBtnClicked(allDrawings[holder.bindingAdapterPosition])
                    }
                    R.id.deleteSavedItem -> {
                        listener.onSavedMenuDeleteBtnClicked(allDrawings[holder.bindingAdapterPosition])
                    }
                }
                return true
            }
        })
    }


    override fun getItemCount(): Int {
        return allDrawings.size
    }

    fun updateList(savedDrawingList: List<SavedItem>){
        allDrawings.clear()
        allDrawings.addAll(savedDrawingList)

        notifyDataSetChanged()
    }

}

interface DrawListener{
    fun onDrawItemClicked(drawItem: SavedItem)
    fun onSavedMenuViewBtnClicked(drawItem: SavedItem)
    fun onSavedMenuEditBtnClicked(drawItem: SavedItem)
    fun onSavedMenuShareBtnClicked(drawItem: SavedItem)
    fun onSavedMenuRenameBtnClicked(drawItem: SavedItem)
    fun onSavedMenuDeleteBtnClicked(drawItem: SavedItem)
}