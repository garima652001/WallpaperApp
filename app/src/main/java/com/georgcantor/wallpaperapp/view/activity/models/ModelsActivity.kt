package com.georgcantor.wallpaperapp.view.activity.models

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.ARG_DETAIL
import com.georgcantor.wallpaperapp.util.Constants.ARG_QUERY
import com.georgcantor.wallpaperapp.view.activity.detail.DetailActivity
import com.georgcantor.wallpaperapp.view.fragment.pictures.PicturesAdapter
import kotlinx.android.synthetic.main.activity_models.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ModelsActivity : AppCompatActivity() {

    private lateinit var viewModel: ModelsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        viewModel = getViewModel { parametersOf() }

        val query = intent?.extras?.get(ARG_QUERY) as String

        with(toolbar) {
            setTitleTextAppearance(this@ModelsActivity, R.style.RalewayTextAppearance)
            title = query
            navigationIcon = getDrawable(this@ModelsActivity, R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                super.onBackPressed()
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
            }
        }

        val adapter = PicturesAdapter {
            openActivity(DetailActivity::class.java) { putParcelable(ARG_DETAIL, it) }
        }
        pictures_recycler.adapter = adapter

        val gridLayoutManager = StaggeredGridLayoutManager(getScreenSize(), VERTICAL)

        pictures_recycler.layoutManager = gridLayoutManager

        val scrollListener = object : EndlessScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                viewModel.getPictures(query, page)
            }
        }
        scrollListener.resetState()

        pictures_recycler.addOnScrollListener(scrollListener)

        with(viewModel) {
            isNetworkAvailable.observe(this@ModelsActivity) { available ->
                no_internet_image.visibility = if (available) View.GONE else View.VISIBLE
            }

            isProgressVisible.observe(this@ModelsActivity) { visible ->
                if (visible) progress_animation.showAnimation() else progress_animation.hideAnimation()
            }

            error.observe(this@ModelsActivity, this@ModelsActivity::shortToast)

            pictures.observe(this@ModelsActivity, adapter::setPictures)

            getPictures(query, 1)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}