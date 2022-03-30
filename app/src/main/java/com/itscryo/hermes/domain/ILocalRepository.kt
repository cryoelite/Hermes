package com.itscryo.hermes.domain

import android.content.Context
import android.graphics.Bitmap
import com.itscryo.hermes.global_model.UserPreferences
import kotlinx.coroutines.Deferred

interface ILocalRepository {
	suspend fun storeUserCredAsync(userID: String, context: Context)

	suspend fun retrieveUserCredAsync(context: Context): String?

	suspend fun storePrefsAsync(userPrefs: UserPreferences, context: Context)

	suspend fun retrievePrefsAsync(context: Context): UserPreferences?

	suspend fun storeImageFromBytesAsync(imageBitmap: Bitmap, imageFileName: String): String

	suspend fun retrieveImageAsync(imageLocation: String): Bitmap?

	suspend fun storeMedia(tempLocalPath: String): String

	suspend fun retrieveMediaFromLocationAsync(mediaLocation: String): Bitmap?

}