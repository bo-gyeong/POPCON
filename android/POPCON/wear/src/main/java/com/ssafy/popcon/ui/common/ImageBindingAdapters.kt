package com.ssafy.popcon.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.ssafy.popcon.R
import java.net.URL

//이미지 url -> view
@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        view.isGone = false
        Glide.with(view)
            .load(imageUrl)
            .into(view)
    } else {
        view.isGone = true
    }
}

//색상 -> background 사용
@BindingConversion
fun convertToColorDrawable(color: String): Drawable {

    return ColorDrawable(Color.parseColor(color))
}

//이미지 url -> 동그라미 표시
@BindingAdapter("circleImageUrl")
fun loadCircleImage(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(view)
            .load(imageUrl)
            .circleCrop()
            .into(view)
    }
}

//이미지 uri -> view
@BindingAdapter("imageUri")
fun loadImageUri(view: ImageView, imageUri: Uri) {
    GlideApp.with(view)
        .load(imageUri)
        .into(view)
}
