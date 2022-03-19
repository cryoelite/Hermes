package com.itscryo.hermes.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


class ImageManager @Inject constructor(@ActivityContext val context: Context) : Service() {
	private val localBinder = LocalBinder()
	companion object{
		const val width= 480
		const val height= 480
	}

	inner class LocalBinder : Binder() {

		suspend fun getImage(imageLocation: String): ByteArray {
			return this@ImageManager.getImage(imageLocation)
		}

		suspend fun downloadImage(url: String): Bitmap {
			return this@ImageManager.downloadImage(url)
		}
	}

	private suspend fun downloadImage(url: String): Bitmap {
		val image= Glide.with(context).load(url).asDeferredAsync().await()
		return image.toCustomBitmap()
	}


	private suspend fun getImage(imageLocation: String): ByteArray {
		val image = Glide.with(context).load(File(imageLocation)).asDeferredAsync().await()

		return image.toByteArray()

	}

	private fun scanImage() {

	}


	override fun onBind(p0: Intent?): IBinder {
		return localBinder;
	}

	private fun Drawable.toByteArray(): ByteArray {
		val bitmap=this.toBitmap(
			width, height,
		)
		val stream= ByteArrayOutputStream ()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
		return  stream.toByteArray()
	}
	private fun Drawable.toCustomBitmap(): Bitmap {
		return toBitmap(
			width, height,
		)
	}

}