package com.itscryo.hermes.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.itscryo.hermes.domain.ICloudStorage
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.global_model.FirestoreCollections
import com.itscryo.hermes.global_model.FirestoreDocuments
import com.itscryo.hermes.global_model.FirestoreFields
import com.itscryo.hermes.global_model.LogTags
import com.itscryo.hermes.global_model.firestore_incoming_model.MessageWithMedia
import com.itscryo.hermes.global_model.message_db_model.MessageWithContent
import com.itscryo.hermes.global_model.message_db_model.UserImage
import com.itscryo.hermes.global_model.message_db_model.UserWithImage
import com.itscryo.hermes.service.asDeferredAsync
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject
import com.itscryo.hermes.global_model.firestore_incoming_model.UserWithImage as UserImageFirestore

@Module
@InstallIn(ActivityComponent::class)
abstract class FirestoreRepositoryModule {

	@Binds
	abstract fun bindFirestoreRepository(
		firestoreImpl: FirestoreRepository
	): IFirestoreRepository
}

class FirestoreRepository @Inject constructor() :
	IFirestoreRepository {
	private val database = Firebase.firestore

	@Inject
	lateinit var cloudStorage: ICloudStorage

	@Inject
	lateinit var localStorage: ILocalRepository

	private val firestoreFields = FirestoreFields()
	private val firestoreDocuments = FirestoreDocuments()
	private val firestoreCollections = FirestoreCollections()
	private val tag = LogTags("FirestoreRepo")


	override suspend fun storeUserInfo(user: UserWithImage) {
		val data = generateUserData(user)
		database.collection(firestoreCollections.users)
			.document(user.user.userID.toString()).set(data)
			.addOnSuccessListener {
				Log.d(
					tag.debug,
					"DocumentSnapshot added"
				)
			}
			.addOnFailureListener { e ->
				Log.e(tag.error, "Error adding document", e)
			}
	}

	private suspend fun getDeviceIDAsync(): Deferred<String> {
		val devID = CompletableDeferred<String>()
		val result = FirebaseInstallations.getInstance().id.asDeferredAsync().await()
		devID.complete(result)
		return devID
	}

	private suspend fun generateUserData(userWithImage: UserWithImage): Map<String, Any> {
		val deviceID = getDeviceIDAsync().await()
		val user = userWithImage.user
		val name = user.name ?: ""
		val imageURL = storeImageAndGetURL(userWithImage.image)
		return mapOf<String, Any>(
			firestoreFields.deviceID to deviceID,
			firestoreFields.isOnline to true,
			firestoreFields.name to name,
			firestoreFields.lastOnline to Timestamp.now(),
			firestoreFields.profileImageURL to imageURL,
			firestoreFields.email to user.email,
		)
	}

	private suspend fun storeImageAndGetURL(image: UserImage): String {

		return cloudStorage.storeProfileImageAndGetURL(image.imageLocation)
	}


	override suspend fun getUserInfo(userID: Long): UserImageFirestore {
		val data =
			database.collection(firestoreCollections.users).document(userID.toString())
				.get().result
		var name: String? = data[firestoreFields.name].toString()
		if (name == null || name.isEmpty())
			name = null
		val profileImageURL = data[firestoreFields.profileImageURL].toString()
		val email = data[firestoreFields.email].toString()
		return UserImageFirestore(
			userID, profileImageURL, email, name
		)


	}

/*	private suspend fun getImageLocationForUser(url: String): String {
		var imageLocation = localStorage.downloadImageAsync(url)
		if (imageLocation == null) {
			imageLocation = localStorage.downloadImageAsync(url)
			if (imageLocation == null) {
				Log.e(tag.error, "Failed to download image even after 2 tries")
				throw Throwable("Failed to download image even after 2 tries")
			}
		}

		return imageLocation
	}*/

	override suspend fun sendMessage(recipientID: Long, userID: Long,message: MessageWithContent) {
		val id="$userID+${GregorianCalendar.getInstance().timeInMillis}"
		database.collection(firestoreCollections.messages).document(firestoreDocuments.messageData).collection(recipientID.toString()).document(id).set(

		)
	}

	private fun MessageWithContent.toFirestoreMessage(userID: Long): MessageWithMedia{
		val message= MessageWithMedia(
			this.content.mediaLocation ?:"",
			textContent = this.message.

		)

	}

	override suspend fun recieveMessages(userID: Long): ReceiveChannel<List<MessageWithMedia>> {
		val channel = Channel<List<MessageWithMedia>>()
		val data = database.collection(firestoreCollections.messages)
			.document(firestoreDocuments.messageData).collection(userID.toString())
		data.addSnapshotListener { snapshot, e ->
			if (e != null) {
				Log.e(tag.error, "Document retrieval failed")
				return@addSnapshotListener
			}

			val list = mutableListOf<MessageWithMedia>()
			for (elem in snapshot!!) {
				val mediaContent = elem[firestoreFields.mediaContent].toString()
				val textContent = elem[firestoreFields.textContent].toString()
				val senderIDLong = when(val tempID=elem[firestoreFields.senderID]){
					is Long -> tempID
					else -> tempID.toString().toLong()
				}


				val message = MessageWithMedia(
					mediaContentLocation = mediaContent,
					textContent,
					senderIDLong
				)
				list.add(message)
			}
			runBlocking {
				channel.send(list)
			}

		}
		return  channel
	}

}