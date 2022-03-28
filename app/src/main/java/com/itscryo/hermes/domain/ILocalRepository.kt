package com.itscryo.hermes.domain

import android.graphics.Bitmap
import com.itscryo.hermes.global_model.UserPreferences
import kotlinx.coroutines.Deferred

interface ILocalRepository {
	suspend fun storeUserCredAsync(userID: String)

	suspend fun retrieveUserCredAsync(): String?

	suspend fun storePrefsAsync(userPrefs: UserPreferences)

	suspend fun retrievePrefsAsync(): UserPreferences?

	suspend fun storeImageFromBytesAsync(imageBitmap: Bitmap): String

	suspend fun retrieveImageAsync(imageLocation: String): Bitmap?

	suspend fun storeMedia(tempLocalPath: String): String

	suspend fun retrieveMediaFromLocationAsync(mediaLocation: String): Bitmap?

}