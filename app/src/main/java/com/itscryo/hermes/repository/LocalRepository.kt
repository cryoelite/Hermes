package com.itscryo.hermes.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.LocalFolders
import com.itscryo.hermes.global_model.LogTags
import com.itscryo.hermes.global_model.UserData
import com.itscryo.hermes.global_model.UserPreferences
import com.itscryo.hermes.service.GlobalEncryption
import com.itscryo.hermes.service.ImageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
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
	private lateinit var imageManagerBinder: ImageManager.LocalBinder
	private val localFolders = LocalFolders()
	private val logTags = LogTags("LocalRepo")
	private val connection = object : ServiceConnection {
		override fun onServiceConnected(className: ComponentName, service: IBinder) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			imageManagerBinder = service as ImageManager.LocalBinder


		}

		override fun onServiceDisconnected(arg0: ComponentName) {

		}

	}

	private fun bind() {
		Intent(context, ImageManager::class.java).also { intent ->
			context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
		}
	}

	private fun unbind() {
		context.unbindService(connection)
	}

	override suspend fun storeUserCredAsync(userData: UserData): Deferred<Unit> {
		val deferred = CompletableDeferred<kotlin.Unit>()
		deferred.complete(Unit)
		return deferred
	}

	override suspend fun storePrefsAsync(userPrefs: UserPreferences): Deferred<Unit> {
		val deferred = CompletableDeferred<kotlin.Unit>()
		deferred.complete(Unit)
		return deferred
	}
	override suspend fun downloadImageAsync(url: String): String? {
		bind()
		var imagePath: String?
		withContext(Dispatchers.IO) {
			try {
				val image = imageManagerBinder.downloadImage(url)
				val imageFIlename =
					GlobalEncryption().generateRandomString() + ".jpg"
				val storageDir = File(localFolders.profilePictureFolder)
				if (!storageDir.exists()) {
					storageDir.mkdirs()
				}
				val imageFile = File(storageDir, imageFIlename)
				imagePath = imageFile.absolutePath

				val fOut = FileOutputStream(imageFile)
				image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
				fOut.close()
			} catch (e: Exception) {
				imagePath=null
				Log.e(logTags.error, e.message ?: "Failed to store image")
			}
			unbind()
		}
		return imagePath
	}

}