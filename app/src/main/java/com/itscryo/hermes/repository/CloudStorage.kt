package com.itscryo.hermes.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import com.itscryo.hermes.domain.ICloudStorage
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.CloudStorageMetadata
import com.itscryo.hermes.global_model.CloudStoragePaths
import com.itscryo.hermes.global_model.LogTags
import com.itscryo.hermes.service.asDeferredAsync
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/*@Module
@InstallIn(SingletonComponent::class)
abstract class CloudStorageModule {

	@Binds
	abstract fun bindCloudStorage(
		cloudStorageImpl: CloudStorage
	): ICloudStorage
}*/

@AssistedFactory
interface CloudStorageFactory {
	fun createCloudStorageService(userID: String): CloudStorage
}

/**
 * Uses AssistedInject, wanted to try it out
 */
class CloudStorage @AssistedInject constructor(@Assisted override var userID: String) :
	ICloudStorage {


	@Inject
	lateinit var localRepository: ILocalRepository
	private val storageInstance = FirebaseStorage.getInstance()
	private val storageRef: StorageReference = storageInstance.reference
	private val cloudStoragePaths = CloudStoragePaths()
	private val cloudStorageMetadata = CloudStorageMetadata()
	private val logTags = LogTags("CloudStorage")


	override suspend fun storeProfileImageAndGetURL(
		imageName: String,
		imageLocalPath: String
	): String {
		val imagesRef = storageRef.child(cloudStoragePaths.users).child(userID.toString())
			.child(cloudStoragePaths.profilePictures)
		val fileRef = imagesRef.child(imageName)
		val imageBytes = getImageBytes(imageLocalPath)
			?: throw Throwable("Failed to load image")
		val uploadTask = fileRef.putBytes(imageBytes)
		uploadTask.addOnFailureListener {
			Log.e(logTags.error, "Failed to upload profile image")
		}.addOnCanceledListener {
			Log.e(logTags.error, "Cancelled uploading profile image")
		}.addOnCompleteListener {
			Log.i(logTags.info, "Successfully uploaded profile image")
		}
		return getProfileImageURL(imageName)
			?: throw Throwable("Failed to store image and retrieve URL")
	}


	private suspend fun getImageBytes(imageLocation: String): ByteArray? {
		val imageBitmap = localRepository.retrieveImageAsync(imageLocation) ?: return null
		val stream = ByteArrayOutputStream()
		imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
		return stream.toByteArray()
	}

	private suspend fun getMediaBytes(mediaLocation: String): ByteArray? {
		val bitmap =
			localRepository.retrieveMediaFromLocationAsync(mediaLocation) ?: return null
		val stream = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
		return stream.toByteArray()
	}


	override suspend fun getProfileImageURL(filename: String): String? {
		val imagesRef = storageRef.child(cloudStoragePaths.users).child(userID.toString())
			.child(cloudStoragePaths.profilePictures)
		val fileRef = imagesRef.child(filename)
		val url = fileRef.downloadUrl.asDeferredAsync().await()
		if (url.path == null) {
			Log.e(logTags.error, "File doesn't exist")
			return null
		}
		return url.path.toString()
	}

	override suspend fun storeMediaAndGetURL(
		mediaFileName: String,
		mediaLocalPath: String,
		secondUserID: String
	): String {
		val mediaRef = storageRef.child(cloudStoragePaths.users).child(userID.toString())
			.child(cloudStoragePaths.media)
		val fileRef = mediaRef.child(mediaFileName)
		val fileBytes =
			getMediaBytes(mediaLocalPath) ?: throw  Throwable("Failed to load media")
		val mediaMetadata = storageMetadata {
			setCustomMetadata(
				cloudStorageMetadata.secondUserID,
				secondUserID
			)
		}
		val uploadTask = fileRef.putBytes(fileBytes, mediaMetadata)
		uploadTask.addOnFailureListener {
			Log.e(logTags.error, "Failed to upload media")
		}.addOnCanceledListener {
			Log.e(logTags.error, "Cancelled uploading media")
		}.addOnCompleteListener {
			Log.i(logTags.info, "Successfully uploaded media")
		}
		return getMediaURL(mediaFileName)
			?: throw Throwable("Failed to store image and retrieve URL")


	}

	override suspend fun getMediaURL(filename: String): String? {
		val mediaRef = storageRef.child(cloudStoragePaths.users).child(userID.toString())
			.child(cloudStoragePaths.media)
		val fileRef = mediaRef.child(filename)
		val url = fileRef.downloadUrl.asDeferredAsync().await()
		if (url.path == null) {
			return null
		} else {
			val metadata = fileRef.metadata.asDeferredAsync().await()
			val secondUserID =
				metadata.getCustomMetadata(cloudStorageMetadata.secondUserID)
					?: return null
			return url.path.toString()
		}
	}
}