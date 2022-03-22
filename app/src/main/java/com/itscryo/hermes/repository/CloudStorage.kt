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
import com.itscryo.hermes.global_model.message_db_model.MessageMedia
import com.itscryo.hermes.global_model.message_db_model.UserImage
import com.itscryo.hermes.service.asDeferredAsync
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import com.itscryo.hermes.global_model.firestore_incoming_model.MessageMedia as FirestoreMedia
import com.itscryo.hermes.global_model.firestore_incoming_model.UserImage as FirestoreUserImage

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


	override suspend fun storeProfileImageAndGetURL(userImage: UserImage): String {
		val imagesRef = storageRef.child(cloudStoragePaths.users).child(userID.toString())
			.child(cloudStoragePaths.profilePictures)
		val fileRef = imagesRef.child(userImage.imageName)
		val imageBytes = getImageBytes(userImage.imageLocalPath)
			?: throw Throwable("Failed to load image")
		val uploadTask = fileRef.putBytes(imageBytes)
		uploadTask.addOnFailureListener {
			Log.e(logTags.error, "Failed to upload profile image")
		}.addOnCanceledListener {
			Log.e(logTags.error, "Cancelled uploading profile image")
		}.addOnCompleteListener {
			Log.i(logTags.info, "Successfully uploaded profile image")
		}
		val url = getProfileImage(userImage.imageName)
			?: throw Throwable("Failed to store image and retrieve URL")
		return url.imageURL
	}


	private suspend fun getImageBytes(imageLocation: String): ByteArray? {
		return localRepository.retrieveImageAsync(imageLocation)
	}

	private suspend fun getMediaBytes(mediaLocation: String): ByteArray? {
		val bitmap =
			localRepository.retrieveMediaFromLocationAsync(mediaLocation) ?: return null
		val stream = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
		return stream.toByteArray()
	}


	override suspend fun getProfileImage(filename: String): FirestoreUserImage? {
		val imagesRef = storageRef.child(cloudStoragePaths.users).child(userID.toString())
			.child(cloudStoragePaths.profilePictures)
		val fileRef = imagesRef.child(filename)
		val url = fileRef.downloadUrl.asDeferredAsync().await()
		if (url.path == null) {
			Log.e(logTags.error, "File doesn't exist")
			return null
		}
		return FirestoreUserImage(filename, url.path.toString())
	}

	override suspend fun storeMediaAndGetURL(media: MessageMedia): String {
		val mediaRef = storageRef.child(cloudStoragePaths.users).child(userID.toString())
			.child(cloudStoragePaths.media)
		if (media.mediaFileName == null || media.mediaLocalPath == null) {
			Log.e(logTags.error, "No media provided")
			throw Throwable("No media provided")
		}
		val fileName = media.mediaFileName as String
		val fileLocation = media.mediaLocalPath as String
		val fileRef = mediaRef.child(fileName)
		val fileBytes =
			getMediaBytes(fileLocation) ?: throw  Throwable("Failed to load media")
		val mediaMetadata = storageMetadata {
			setCustomMetadata(
				cloudStorageMetadata.secondUserID,
				media.secondUserIDMedia.toString()
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
		val url = getMedia(fileName)
			?: throw Throwable("Failed to store image and retrieve URL")
		return url.mediaURL


	}

	override suspend fun getMedia(filename: String): FirestoreMedia? {
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
			return FirestoreMedia(url.path.toString(), filename, secondUserID.toLong())
		}
	}
}