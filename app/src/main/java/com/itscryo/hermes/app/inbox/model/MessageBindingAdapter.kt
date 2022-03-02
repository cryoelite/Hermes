package com.itscryo.hermes.app.inbox.model

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("setCustomImage")
fun ImageView.setCustomImage(image: Drawable) {
	setImageDrawable(image)
}
