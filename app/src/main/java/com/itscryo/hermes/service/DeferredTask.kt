package com.itscryo.hermes.service

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