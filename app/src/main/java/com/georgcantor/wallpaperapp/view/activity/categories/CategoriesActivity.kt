package com.georgcantor.wallpaperapp.view.activity.categories

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.GridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.view.activity.models.ModelsActivity
import kotlinx.android.synthetic.main.activity_categories.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        viewModel = getViewModel { parametersOf() }

        with(toolbar) {
            setTitleTextAppearance(this@CategoriesActivity, R.style.RalewayTextAppearance)
            title = getString(R.string.categories)
            navigationIcon = getDrawable(this@CategoriesActivity, R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                super.onBackPressed()
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
            }
        }

        categories_recycler.setHasFixedSize(true)
        categories_recycler.layoutManager = GridLayoutManager(this, this.getScreenSize())

        with(viewModel) {
            getCategories()

            isNetworkAvailable.observe(this@CategoriesActivity) { available ->
                no_internet_image.visibility = if (available) View.GONE else View.VISIBLE
            }

            isProgressVisible.observe(this@CategoriesActivity) { visible ->
                if (visible) progress_animation.showAnimation() else progress_animation.hideAnimation()
            }

            error.observe(this@CategoriesActivity) { shortToast(it) }

            categories.observe(this@CategoriesActivity) {
                categories_recycler.adapter = CategoriesAdapter(it) {
                    openActivity(ModelsActivity::class.java) { putString(Constants.ARG_QUERY, it.categoryName) }
                }
            }

            refresh_layout.setOnRefreshListener {
                getCategories()
                refresh_layout.isRefreshing = false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}