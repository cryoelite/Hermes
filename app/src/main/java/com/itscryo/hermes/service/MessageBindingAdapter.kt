package com.itscryo.hermes.app.inbox.model

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData

@BindingAdapter("setCustomImage")
fun ImageView.setCustomImage(imageID: Int) {
	val image= ResourcesCompat.getDrawable(resources, imageID, null)
	setImageDrawable(image)
}

@BindingAdapter("setDrawable")
fun ImageView.setDrawable(image: Drawable) {
	setImageDrawable(image)
}



