package com.itscryo.hermes.repository

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.FileUtils
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.*
import com.itscryo.hermes.service.GlobalEncryption
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalRepoModule {

	@Singleton
	@Binds
	abstract fun provideLocalRepo(localRepository: LocalRepository): ILocalRepository
}


class LocalRepository @Inject constructor(@ActivityContext val context: Context) :
	ILocalRepository {
	private val localFolders = LocalFolders()
	private val logTags = LogTags("LocalRepo")
	private val sharedPrefKeys = SharedPrefKeys()


	override suspend fun storeUserCredAsync(userID: String) {

		withContext(Dispatchers.Default) {
			val sharedPrefs = getEncryptedSharedPrefs()
			with(sharedPrefs.edit())
			{
				putString(sharedPrefKeys.userID, userID)
				apply()
			}
		}

	}

	private fun getEncryptedSharedPrefs(): SharedPreferences {
		val masterKey = MasterKey.Builder(context).build()

		return EncryptedSharedPreferences.create(
			context,
			sharedPrefKeys.credFileName,
			masterKey,
			EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
			EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
		)
	}

	override suspend fun retrieveUserCredAsync(): String? {
		return withContext<String?>(Dispatchers.Default) {
			try {
				val sharedPrefs = getEncryptedSharedPrefs()
				val data =
					sharedPrefs.getString(sharedPrefKeys.userID, null)
				if (data != null) {
					Log.i(
						logTags.info,
						"Successfully retrieved user data from Shared Prefs"
					)
					return@withContext data
				} else {
					Log.i(
						logTags.info,
						"Failed to find user data in shared prefs"
					)
					return@withContext null
				}
			} catch (e: Exception) {
				Log.e(
					logTags.error,
					e.message
						?: "Failed to retrieve key from shared prefs"
				)
				return@withContext null
			}

		}


	}

	private fun getSharedPrefs(): SharedPreferences {
		return context.getSharedPreferences(sharedPrefKeys.spFileName, Context.MODE_PRIVATE)
	}

	override suspend fun storePrefsAsync(userPrefs: UserPreferences) {
		withContext(Dispatchers.Default)
		{
			val sharedPrefs = getSharedPrefs()
			with(sharedPrefs.edit()) {
				putString(sharedPrefKeys.appTheme, userPrefs.appTheme)
				putString(sharedPrefKeys.appLanguage, userPrefs.appLanguage)
				apply()
			}
		}
	}

	override suspend fun retrievePrefsAsync(): UserPreferences? {
		return withContext<UserPreferences?>(Dispatchers.Default) {
			try {
				val sharedPrefs = getSharedPrefs()
				val appTheme =
					sharedPrefs.getString(sharedPrefKeys.appTheme, null)
				val appLanguage =
					sharedPrefs.getString(sharedPrefKeys.appTheme, null)

				if (appTheme != null && appLanguage != null) {
					Log.i(
						logTags.info,
						"Successfully retrieved user preferences from Shared Prefs"
					)
					return@withContext UserPreferences(appTheme, appLanguage)
				} else {
					Log.i(
						logTags.info,
						"Failed to find user preferences in shared prefs"
					)
					return@withContext null
				}
			} catch (e: Exception) {
				Log.e(
					logTags.error,
					e.message
						?: "Failed to retrieve key from shared prefs"
				)
				return@withContext null
			}

		}
	}


	override suspend fun storeImageFromBytesAsync(imageBitmap: Bitmap): String {
		val imagePath =withContext<String?>(Dispatchers.IO) {
				try {
					val imageFIlename =
						GlobalEncryption().generateRandomString() + ".jpg"
					val storageDir = File(localFolders.profilePictureFolder)
					if (!storageDir.exists()) {
						storageDir.mkdirs()
					}
					val imageFile = File(storageDir, imageFIlename)

					val fOut = FileOutputStream(imageFile)
					imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
					fOut.close()
					Log.i(logTags.info, "Image stored successfully")
					return@withContext imageFile.absolutePath
				} catch (e: Exception) {

					Log.e(logTags.error, e.message ?: "Failed to store image")
					return@withContext null
				}
			}?: throw Throwable("Failed to store Image and get image path")
		return imagePath
	}


	override suspend fun retrieveImageAsync(imageLocation: String): Bitmap? {
		val mediaFile = File(imageLocation)
		if (!mediaFile.exists())
			return null
		try {
			val image = withContext<Bitmap>(Dispatchers.IO) {
				return@withContext BitmapFactory.decodeFile(imageLocation)
			}

			return image

		} catch (e: Exception) {
			Log.e(logTags.error, "Failed to retrieve image from local directory")
			return null
		}
	}

	override suspend fun storeMedia(tempLocalPath: String): String {
			val location = File(tempLocalPath)
			val destination = File(localFolders.mediaContent)
			if (!destination.exists()) {
				destination.mkdirs()
			}
			withContext(Dispatchers.IO) {
				try {
					FileUtils.copy(
						location.inputStream(),
						destination.outputStream()
					)
					Log.i(logTags.info, "Successfully stored media from URL")
				} catch (e: Exception) {
					Log.e(logTags.error, "Failed to store media from URL")
				}
			}
			return destination.absolutePath

	}


	override suspend fun retrieveMediaFromLocationAsync(mediaLocation: String): Bitmap? = retrieveImageAsync(mediaLocation)

}