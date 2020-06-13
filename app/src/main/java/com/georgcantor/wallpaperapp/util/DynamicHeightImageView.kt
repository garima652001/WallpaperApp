package com.georgcantor.wallpaperapp.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class DynamicHeightImageView : AppCompatImageView {

    private var whRatio = 0F

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setRatio(ratio: Float) {
        whRatio = ratio
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (whRatio != 0F) {
            val width = measuredWidth
            val height = (whRatio * width).toInt()
            setMeasuredDimension(width, height)
        }
    }
}
