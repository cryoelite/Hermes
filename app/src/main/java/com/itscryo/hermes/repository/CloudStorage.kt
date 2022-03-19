package com.itscryo.hermes.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.itscryo.hermes.domain.ICloudStorage
import com.itscryo.hermes.global_model.CloudStoragePaths
import com.itscryo.hermes.global_model.LogTags
import com.itscryo.hermes.service.GlobalEncryption
import com.itscryo.hermes.service.ImageManager
import com.itscryo.hermes.service.asDeferredAsync
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@Module
@InstallIn(ActivityComponent::class)
abstract class CloudStorageModule {

	@Binds
	abstract fun bindCloudStorage(
		cloudStorageImpl: CloudStorage
	): ICloudStorage
}

class CloudStorage @Inject constructor(@ActivityContext val context: Context) : ICloudStorage {
	private lateinit var imageManagerBinder: ImageManager.LocalBinder

	private val storageInstance = FirebaseStorage.getInstance()
	private val storageRef: StorageReference = storageInstance.reference
	private val cloudStoragePaths = CloudStoragePaths()
	private val logTags = LogTags("CloudStorage")
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


	override suspend fun storeProfileImageAndGetURL(imageLocation: String): String {
		val filename = GlobalEncryption().generateRandomString() + ".jpg"
		val imagesRef = storageRef.child(cloudStoragePaths.profilePictures)
		val fileRef = imagesRef.child(filename)
		val imageBytes = getImageBytes(imageLocation)

		val uploadTask = fileRef.putBytes(imageBytes)
		uploadTask.addOnFailureListener {
			Log.e(logTags.error, "Failed to upload profile image")
		}.addOnCanceledListener {
			Log.e(logTags.error, "Cancelled uploading profile image")
		}.addOnCompleteListener {
			Log.i(logTags.info, "Successfully uploaded profile image")
		}

		return getProfileImageURL(filename)


	}


	private suspend fun getImageBytes(imageLocation: String): ByteArray {
		val byteArray: ByteArray
		bind()
		val result = imageManagerBinder.getImage(imageLocation)
		unbind()
		return result
	}

	override suspend fun getProfileImageURL(filename: String): String {
		val imagesRef = storageRef.child(cloudStoragePaths.profilePictures)
		val fileRef = imagesRef.child(filename)
		val url = fileRef.downloadUrl.asDeferredAsync().await()
		if (url.path == null) {
			Log.e(logTags.error, "File doesn't exist")
			throw Exception("File doesn't exist")
		}
		return url.path.toString()
	}

}