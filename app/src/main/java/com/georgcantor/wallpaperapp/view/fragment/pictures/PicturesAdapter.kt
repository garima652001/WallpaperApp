package com.georgcantor.wallpaperapp.view.fragment.pictures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.DynamicHeightImageView
import com.georgcantor.wallpaperapp.util.loadImage
import kotlinx.android.synthetic.main.item_picture.view.*

class PicturesAdapter(private val clickListener: (CommonPic) -> Unit) :
    RecyclerView.Adapter<PicturesAdapter.PictureViewHolder>() {

    private val commonPics = mutableListOf<CommonPic>()

    fun setPictures(pictures: MutableList<CommonPic>) {
        this.commonPics.addAll(pictures)
        notifyDataSetChanged()
    }

    fun clearPictures() {
        commonPics.clear()
        notifyItemRangeRemoved(0, commonPics.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PictureViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_picture, null))

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val pic = commonPics[position]
        val params = holder.imageView.layoutParams as RelativeLayout.LayoutParams
        val height = pic.height?.toFloat()
        val width = pic.width?.toFloat()
        val ratio = width.let { height?.div(it ?: 0F) } ?: 0F

        params.height = (params.width * ratio).toInt()
        holder.imageView.layoutParams = params
        holder.imageView.setRatio(ratio)

        holder.itemView.context.loadImage(pic.url, holder.imageView, null)

        holder.itemView.setOnClickListener { clickListener(pic) }
    }

    override fun getItemCount(): Int = commonPics.size

    class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: DynamicHeightImageView = itemView.picture_image
    }
}