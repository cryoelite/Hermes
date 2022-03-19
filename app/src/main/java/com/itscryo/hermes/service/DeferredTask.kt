package com.itscryo.hermes.service

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

fun <T> Task<T>.asDeferredAsync(): Deferred<T> {
	val deferred = CompletableDeferred<T>()

	deferred.invokeOnCompletion {
		if (deferred.isCancelled) {
			deferred.completeExceptionally(Throwable("Cancelled"))
		}
	}

	this.addOnSuccessListener { result -> deferred.complete(result) }
	this.addOnFailureListener { exception -> deferred.completeExceptionally(exception) }

	return deferred
}


suspend fun <T> RequestBuilder<T>.asDeferredAsync(): Deferred<T> {
	val deferred = CompletableDeferred<T>()

	deferred.invokeOnCompletion {
		if (deferred.isCancelled) {
			deferred.completeExceptionally(Throwable("Cancelled"))
		}
	}

	this.listener(object : RequestListener<T> {
		override fun onLoadFailed(
			e: GlideException?,
			model: Any?,
			target: Target<T>?,
			isFirstResource: Boolean
		): Boolean {
			deferred.completeExceptionally(Throwable("Failed to load image"))
			return false
		}

		override fun onResourceReady(
			resource: T,
			model: Any?,
			target: Target<T>?,
			dataSource: DataSource?,
			isFirstResource: Boolean
		): Boolean {
			deferred.complete(resource)
			return true
		}

	}).submit()





	return deferred
}
