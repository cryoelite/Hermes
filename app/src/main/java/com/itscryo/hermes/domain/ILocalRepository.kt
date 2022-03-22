package com.itscryo.hermes.domain

import android.graphics.Bitmap
import com.itscryo.hermes.global_model.UserPreferences
import kotlinx.coroutines.Deferred

interface ILocalRepository {
	suspend fun storeUserCredAsync(userID: String)

	suspend fun retrieveUserCredAsync(): String?

	suspend fun storePrefsAsync(userPrefs: UserPreferences)

	suspend fun retrievePrefsAsync(): UserPreferences?

	suspend fun storeImageFromURLAsync(url: String): String

	suspend fun retrieveImageAsync(imageLocation: String): ByteArray?

	suspend fun storeMediaFromURL(url: String): String

	suspend fun retrieveMediaFromLocationAsync(mediaLocation: String): Bitmap?

}