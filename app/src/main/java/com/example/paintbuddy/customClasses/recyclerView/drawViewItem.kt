package com.example.paintbuddy.customClasses.recyclerView

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.paintbuddy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.draw_item_view.view.*


class drawViewItem(val context: Context, private val Id: String, private val title: String, private val drawUrl: String) : Item<GroupieViewHolder>() {
    val id = Id
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.titleTextView.text = title
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)

        Glide.with(context).load(drawUrl).apply(options).into(viewHolder.itemView.drawThumbImageView)
    }

    override fun getLayout(): Int {
        return R.layout.draw_item_view
    }
}