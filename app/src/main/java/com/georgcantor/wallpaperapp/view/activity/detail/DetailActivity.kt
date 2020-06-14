package com.georgcantor.wallpaperapp.view.activity.detail

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ablanco.zoomy.Zoomy
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.ARG_DETAIL
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {

    private var permissionCheck: Int = 0
    private var picture: CommonPic? = null

    private lateinit var viewModel: DetailViewModel
    private lateinit var zoomyBuilder: Zoomy.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        viewModel = getViewModel { parametersOf() }

        permissionCheck = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)

        picture = intent?.extras?.getParcelable(ARG_DETAIL) as CommonPic?

        progress_animation.showAnimation()

        loadImage(picture?.fullHdUrl ?: "", image, progress_animation)

        with(viewModel) {
            checkIsFavourite(picture?.url ?: "")

            isProgressVisible.observe(this@DetailActivity) {
                when (it) {
                    true -> similar_progress.showAnimation()
                    false -> similar_progress.hideAnimation()
                }
            }

            isWallProgressVisible.observe(this@DetailActivity) {
                when (it) {
                    true -> progress_animation.showAnimation()
                    false -> progress_animation.hideAnimation()
                }
            }

            error.observe(this@DetailActivity, this@DetailActivity::shortToast)

            getSimilarImages(picture?.tag ?: "")

            similarImages.observe(this@DetailActivity) {
                similar_recycler.adapter = SimilarAdapter(it) {
                    openActivity(DetailActivity::class.java) { putParcelable(ARG_DETAIL, it) }
                }
            }

            isFavorite.observe(this@DetailActivity) { favorite ->
                bottom_app_bar.menu.findItem(R.id.action_add_to_fav).setIcon(
                    if (favorite) R.drawable.ic_star_red else R.drawable.ic_star_border
                )

                bottom_app_bar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_share -> {
                        }
                        R.id.action_download -> {
                        }
                        R.id.action_add_to_fav -> {
                            when (favorite) {
                                true -> {
                                    viewModel.setFavoriteStatus(picture)
                                    unstar_anim.showSingleAnimation(1.5F)
                                }
                                false -> {
                                    viewModel.setFavoriteStatus(picture)
                                    star_anim.showSingleAnimation(1F)
                                }
                            }
                        }
                    }
                    false
                }

                zoomyBuilder = Zoomy.Builder(this@DetailActivity)
                    .target(image)
                    .doubleTapListener {
                        picture?.let {
                            when (favorite) {
                                true -> {
                                    viewModel.setFavoriteStatus(picture)
                                    unstar_anim.showSingleAnimation(1.5F)
                                }
                                false -> {
                                    viewModel.setFavoriteStatus(picture)
                                    star_anim.showSingleAnimation(1F)
                                }
                            }
                        }
                    }
                zoomyBuilder.register()
            }

            isWallpaperSet.observe(this@DetailActivity) { isSet ->
                if (isSet) shortToast(getString(R.string.set_wall_complete))
            }

            uri.observe(this@DetailActivity) {
                try {
                    startActivity(Intent(WallpaperManager.getInstance(baseContext).getCropAndSetWallpaperIntent(it)))
                } catch (e: IllegalArgumentException) {
                    val bitmap = getBitmap(contentResolver, it)
                    setAsWallpaper(bitmap)
                }
            }

            fab.setOnClickListener {
                isWallProgressVisible.value = true

                if (this@DetailActivity.isNetworkAvailable()) {
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        CoroutineScope(Dispatchers.IO).launch { setImageAsWallpaper(picture) }
                    } else {
                        ActivityCompat.requestPermissions(
                            this@DetailActivity,
                            arrayOf(WRITE_EXTERNAL_STORAGE),
                            102
                        )
                    }
                } else {
                    longToast(getString(R.string.no_internet))
                }
            }
        }

        bottom_app_bar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            CoroutineScope(Dispatchers.IO).launch { viewModel.setImageAsWallpaper(picture) }
        } else {
            longToast(getString(R.string.you_need_perm_toast))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }
}
