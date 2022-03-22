package com.itscryo.hermes.repository

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.FileUtils
import android.os.IBinder
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.*
import com.itscryo.hermes.service.GlobalEncryption
import com.itscryo.hermes.service.MediaService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@Module
@InstallIn(ActivityComponent::class)
abstract class LocalRepoModule {

	@Binds
	abstract fun bindLocalRepo(
		localImpl: LocalRepository
	): ILocalRepository
}


class LocalRepository @Inject constructor(@ActivityContext val context: Context) :
	ILocalRepository {
	private lateinit var imageManagerBinder: MediaService.LocalBinder
	private val localFolders = LocalFolders()
	private val logTags = LogTags("LocalRepo")
	private val sharedPrefKeys = SharedPrefKeys()
	private val connection = object : ServiceConnection {
		override fun onServiceConnected(className: ComponentName, service: IBinder) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			imageManagerBinder = service as MediaService.LocalBinder


		}

		override fun onServiceDisconnected(arg0: ComponentName) {

		}

	}

	private suspend fun <T> withBind(lambda: suspend () -> T): T {
		bind()
		val result = lambda()
		unbind()
		return result
	}

	private fun bind() {
		Intent(context, MediaService::class.java).also { intent ->
			context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
		}
	}

	private fun unbind() {
		context.unbindService(connection)
	}

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

				if (appTheme != null && appLanguage!=null) {
					Log.i(
						logTags.info,
						"Successfully retrieved user preferences from Shared Prefs"
					)
					return@withContext UserPreferences(appTheme,appLanguage)
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


	override suspend fun storeImageFromURLAsync(url: String): String {
		val imagePath = withBind<String?> {
			val imagePath: String? = withContext<String?>(Dispatchers.IO) {
				try {
					val image = imageManagerBinder.downloadImage(url)
						?: return@withContext null
					val imageFIlename =
						GlobalEncryption().generateRandomString() + ".jpg"
					val storageDir = File(localFolders.profilePictureFolder)
					if (!storageDir.exists()) {
						storageDir.mkdirs()
					}
					val imageFile = File(storageDir, imageFIlename)

					val fOut = FileOutputStream(imageFile)
					image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
					fOut.close()
					Log.i(logTags.info, "Image stored successfully")
					return@withContext imageFile.absolutePath
				} catch (e: Exception) {

					Log.e(logTags.error, e.message ?: "Failed to store image")
					return@withContext null
				}
			}
			return@withBind imagePath
		} ?: throw Throwable("Failed to store Image and get image path")
		return imagePath
	}


	override suspend fun retrieveImageAsync(imageLocation: String): ByteArray? {
		val image: ByteArray? = withBind<ByteArray?> {
			return@withBind imageManagerBinder.getImage(imageLocation)
		}
		return image
	}

	override suspend fun storeMediaFromURL(url: String): String {
		val dataTempLocation = withBind<String?> {
			return@withBind imageManagerBinder.downloadMedia(url)
		}
		if (dataTempLocation != null) {
			val location = File(dataTempLocation)
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
		} else {
			Log.e(logTags.error, "Failed to download media from URL")
			throw Throwable("Failed to download media from URL")
		}
	}


	override suspend fun retrieveMediaFromLocationAsync(mediaLocation: String): Bitmap? {
		val mediaFile = File(mediaLocation)
		if (!mediaFile.exists())
			return null
		try {
			val image = withContext<Bitmap>(Dispatchers.IO) {
				return@withContext BitmapFactory.decodeFile(mediaLocation)
			}

			return image

		} catch (e: Exception) {
			Log.e(logTags.error, "Failed to retrieve media from local directory")
			return null
		}
	}

}