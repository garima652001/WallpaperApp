package com.georgcantor.wallpaperapp.view.activity.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.util.loadImage
import kotlinx.android.synthetic.main.item_category.view.*

class CategoriesAdapter(
    categories: MutableList<Category>,
    private val clickListener: (Category) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private val categories = mutableListOf<Category>()

    init {
        this.categories.addAll(categories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, null)
        )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        with(holder) {
            categoryName.text = category.categoryName

            itemView.context.loadImage(
                category.categoryUrl,
                holder.categoryImage,
                null
            )

            itemView.setOnClickListener {
                clickListener(category)
            }
        }
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryImage: ImageView = itemView.category_image
        var categoryName: TextView = itemView.category_name
    }
}
