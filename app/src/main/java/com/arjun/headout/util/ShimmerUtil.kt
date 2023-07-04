package com.arjun.headout.util

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

object ShimmerUtil {

    private val defaultBaseColor = Color.parseColor("#444444")
    private val defaultHighlightColor = Color.parseColor("#CCCCCC")
    private const val defaultDuration = 1800L
    private const val defaultDirection = Shimmer.Direction.LEFT_TO_RIGHT
    private const val defaultAutoStart = true
    private const val defaultSizeMultiplier = 0.2f

    fun loadImageWithShimmer(
        imageView: View, imageUrl: String?, placeholderDrawable: Drawable? = null
    ) {

        val shimmer = Shimmer.ColorHighlightBuilder().setDuration(defaultDuration)
            .setBaseColor(defaultBaseColor).setHighlightColor(defaultHighlightColor)
            .setDirection(defaultDirection).setAutoStart(defaultAutoStart).build()

        val shimmerDrawable = ShimmerDrawable()
        shimmerDrawable.setShimmer(shimmer)

        val requestBuilder =
            Glide.with(imageView.context).asDrawable().sizeMultiplier(defaultSizeMultiplier)

        Glide.with(imageView.context).load(imageUrl).thumbnail(requestBuilder)
            .apply(RequestOptions().placeholder(shimmerDrawable)).error(placeholderDrawable)
            .into(imageView as ImageView)
    }

}