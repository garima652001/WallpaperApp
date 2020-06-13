package com.georgcantor.wallpaperapp.view.fragment.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.util.DynamicHeightImageView
import com.georgcantor.wallpaperapp.util.loadImage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_favorite.view.*

class FavoritesAdapter(
    favorites: MutableList<Favorite>,
    private val isNotGrid: Boolean,
    private val clickListener: (Favorite) -> Unit,
    private val longClickListener: (Favorite) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    private val favorites: MutableList<Favorite>? = ArrayList()

    init {
        clearPictures()
        this.favorites?.addAll(favorites)
        notifyDataSetChanged()
    }

    private fun clearPictures() {
        val size = favorites?.size
        favorites?.clear()
        size?.let { notifyItemRangeRemoved(0, it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavoritesViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, null)
    )

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val favorite = favorites?.get(position)
        val hitJson = favorite?.image
        val pic = Gson().fromJson(hitJson, CommonPic::class.java)

        val layoutParams = holder.imageView.layoutParams as RelativeLayout.LayoutParams
        val height = pic.height?.toFloat()
        val width = pic.width?.toFloat()
        val ratio = height?.div(width ?: 0F) ?: 0F
        layoutParams.height = (layoutParams.width * ratio).toInt()

        with(holder) {
            imageView.layoutParams = layoutParams
            imageView.setRatio(ratio)

            imageView.setOnClickListener {
                favorite?.let { clickListener(it) }
            }

            imageView.setOnLongClickListener {
                favorite?.let { longClickListener(it) }
                false
            }

            itemView.context.loadImage(
                if (isNotGrid) pic.fullHdUrl ?: "" else pic.url,
                holder.imageView,
                null
            )
        }
    }

    override fun getItemCount(): Int = favorites?.size ?: 0

    class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: DynamicHeightImageView = itemView.favorite_image
    }
}