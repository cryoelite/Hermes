package com.itscryo.hermes.service

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.itscryo.hermes.global_model.LogTags
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject


class MediaService @Inject constructor(@ActivityContext val context: Context) : Service() {
	private val localBinder = LocalBinder()
	private val tags = LogTags("MediaService")

	companion object {
		const val width = 480
		const val height = 480
	}

	inner class LocalBinder : Binder() {

		suspend fun getImage(imageLocation: String): ByteArray? {
			return this@MediaService.getImage(imageLocation)
		}

		suspend fun downloadImage(url: String): Bitmap? {
			return this@MediaService.downloadImage(url)
		}

		suspend fun downloadMedia(url: String): String? {
			return this@MediaService.downloadMedia(url)
		}

	}

	private suspend fun downloadMedia(url: String): String? {
		return try {
			val mediaLocation = withContext<String>(Dispatchers.IO) {
				val downloadManager =
					context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
				val downloadUri = Uri.parse(url)
				val tempFileName =
					"${GregorianCalendar.getInstance().timeInMillis}.jpg "
				val request = DownloadManager.Request(downloadUri).apply {
					setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE).setAllowedOverRoaming(
						true
					).setTitle(tempFileName).setDescription("")
				}
				val downloadID = downloadManager.enqueue(request)
				val query = DownloadManager.Query().setFilterById(downloadID)
				var isDownloading = true
				while (isDownloading) {
					val cursor = downloadManager.query(query)
					cursor.moveToFirst()
					if (cursor.getInt(
							cursor.getColumnIndexOrThrow(
								DownloadManager.COLUMN_STATUS
							)
						) == DownloadManager.STATUS_SUCCESSFUL
					) {
						isDownloading = false
					}
				}
				return@withContext downloadManager.getUriForDownloadedFile(
					downloadID
				).path
					?: throw Throwable("Couldn't get download location for downloaded file")
			}
			mediaLocation

		} catch (e: Exception) {
			Log.e(tags.error, e.message ?: "Failed to download media")
			null
		}
	}

	private suspend fun downloadImage(url: String): Bitmap? {
		return try {
			val image = Glide.with(context).load(url).asDeferredAsync().await()
			image.toCustomBitmap()
		} catch (e: Exception) {
			Log.e(tags.error, e.message ?: "Failed to download image")
			null
		}
	}


	private suspend fun getImage(imageLocation: String): ByteArray? {
		return try {
			val image = Glide.with(context).load(File(imageLocation)).asDeferredAsync()
				.await()

			image.toByteArray()
		}
		catch(e: Exception)
		{
			Log.e(tags.error,e.message ?: "Failed to retrieve image")
			null
		}
	}


	override fun onBind(p0: Intent?): IBinder {
		return localBinder
	}

	private fun Drawable.toByteArray(): ByteArray {
		val bitmap = this.toBitmap(
			width, height,
		)
		val stream = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
		return stream.toByteArray()
	}

	private fun Drawable.toCustomBitmap(): Bitmap {
		return toBitmap(
			width, height,
		)
	}

}