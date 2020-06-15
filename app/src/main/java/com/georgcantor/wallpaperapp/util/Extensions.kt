package com.georgcantor.wallpaperapp.util

import android.animation.Animator
import android.content.ContentValues
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection.scanFile
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.os.Handler
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.*
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.util.Constants.COUNTER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val context = this as AppCompatActivity
    Intent(this, it).apply {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
    context.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
}

fun Context.isNetworkAvailable() = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)
    ?.activeNetworkInfo?.isConnectedOrConnecting ?: false

fun Context.shortToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.getScreenSize(): Int =
    when (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
        Configuration.SCREENLAYOUT_SIZE_XLARGE -> 4
        Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> 3
        Configuration.SCREENLAYOUT_SIZE_LARGE -> 3
        Configuration.SCREENLAYOUT_SIZE_NORMAL -> 2
        Configuration.SCREENLAYOUT_SIZE_SMALL -> 2
        else -> 2
    }

fun Context.showDialog(
    isRating: Boolean,
    message: String?,
    function: () -> (Unit)
) {
    val dialog = AlertDialog.Builder(this)
    val preferenceManager = PreferenceManager(this)

    when (isRating) {
        true -> {
            val linearLayout = LinearLayout(this)
            val ratingBar = RatingBar(this)
            var userMark = 0

            val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            params.setMargins(50, 20, 0, 0)
            ratingBar.layoutParams = params
            ratingBar.numStars = 5
            ratingBar.stepSize = 1F

            linearLayout.addView(ratingBar)

            dialog.setTitle(this.getString(R.string.rate_us))
            dialog.setView(linearLayout)

            ratingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _, ratingNumber, _ ->
                    userMark = ratingNumber.toInt()
                }

            dialog
                .setPositiveButton(R.string.add_review) { _, _ ->
                    when (userMark) {
                        in 4..5 -> {
                            function()
                            preferenceManager.saveInt(COUNTER, 10)
                        }
                        else -> this.shortToast(getString(R.string.thanks_for_feedback))
                    }
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                    dialogInterface.cancel()
                    preferenceManager.saveInt(COUNTER, 0)
                }
        }
        false -> {
            dialog
                .setMessage(message)
                .setNegativeButton(R.string.no) { _, _ -> }
                .setPositiveButton(R.string.yes) { _, _ -> function() }
        }
    }
    dialog.create()
    dialog.show()
}

fun Context.openUrl(url: String) = Intent(ACTION_VIEW, Uri.parse(url)).apply {
    addFlags(FLAG_ACTIVITY_NEW_TASK)
    ContextCompat.startActivity(this@openUrl, this, null)
}

fun Context.loadImage(
    url: String,
    view: ImageView,
    animView: LottieAnimationView?
) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.placeholder)
        .thumbnail(0.1F)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                animView?.hideAnimation()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                animView?.hideAnimation()
                return false
            }
        })
        .into(view)
}

fun Context.saveImage(url: String?) {
    Glide.with(this)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            bitmap.saveImageQ(this@saveImage)
                        } else {
                            bitmap.saveImage(this@saveImage)
                        }
                    }
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}

private fun Bitmap.saveImage(context: Context) {
    val root = getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString()
    val myDir = File("$root/Wallpapers")
    myDir.mkdirs()
    val randomInt = (0..10000).random()
    val fileName = "Image-$randomInt.jpg"
    val file = File(myDir, fileName)
    if (file.exists()) file.delete()
    try {
        FileOutputStream(file).apply {
            compress(Bitmap.CompressFormat.JPEG, 90, this)
            flush()
            close()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    scanFile(context, arrayOf(file.toString()), null) { path, uri ->
        Log.i("ExternalStorage", "Scanned $path:")
        Log.i("ExternalStorage", "-> uri=$uri")
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.saveImageQ(context: Context) {
    val values = contentValues()
    values.put(RELATIVE_PATH, "Pictures/" + "Wallpapers")
    values.put(IS_PENDING, true)

    val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        saveImageToStream(this, context.contentResolver.openOutputStream(uri))
        values.put(IS_PENDING, false)
        context.contentResolver.update(uri, values, null, null)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun contentValues() = ContentValues().apply {
    put(MIME_TYPE, "image/png")
    put(DATE_ADDED, currentTimeMillis() / 1000)
    put(DATE_TAKEN, currentTimeMillis())
}

private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
    if (outputStream != null) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun View.visible() { visibility = VISIBLE }

fun View.gone() { visibility = GONE }

fun View.hideKeyboard() =
    (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)
    }

fun View.showKeyboard() =
    (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        requestFocus()
        showSoftInput(this@showKeyboard, 0)
    }

fun LottieAnimationView.showAnimation() {
    visible()
    playAnimation()
    loop(true)
}

fun LottieAnimationView.hideAnimation() {
    cancelAnimation()
    gone()
}

fun LottieAnimationView.showSingleAnimation(speed: Float) {
    visible()
    playAnimation()
    repeatCount = 0
    this.speed = speed
    addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            gone()
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }
    })
}

inline fun <T> LiveData<T>.observe(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    this.observe(owner, Observer { it?.apply(observer) })
}

fun runDelayed(delay: Long, action: () -> Unit) {
    Handler().postDelayed(action, TimeUnit.MILLISECONDS.toMillis(delay))
}