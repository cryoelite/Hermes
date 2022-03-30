package com.itscryo.hermes.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
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
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import com.itscryo.hermes.global_model.firestore_incoming_model.UserWithImage as UserImageFirestore

@Module
@InstallIn(SingletonComponent::class)
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
	lateinit var cloudStorageFactory: CloudStorageFactory

	@Inject
	lateinit var localStorage: ILocalRepository

	private val firestoreFields = FirestoreFields()
	private val firestoreDocuments = FirestoreDocuments()
	private val firestoreCollections = FirestoreCollections()
	private val tag = LogTags("FirestoreRepo")


	override suspend fun storeUserInfo(user: UserWithImage) {
		val data = generateUserData(user)
		database.collection(firestoreCollections.users)
			.document(user.user.userID).set(data)
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

	private suspend fun getFirestoreDeviceID(): String {
		val devID = CompletableDeferred<String>()
		val result = FirebaseInstallations.getInstance().id.asDeferredAsync().await()
		devID.complete(result)
		return devID.await()
	}

	override suspend fun isDeviceIDSame(userID: String): Boolean {
		try {
			val data =
				database.collection(firestoreCollections.users)
					.document(userID)
					.get().result
			val deviceID= data[firestoreFields.deviceID]
			val thisDeviceID= getFirestoreDeviceID()
			if(deviceID!=null && deviceID==thisDeviceID) {
				return true
			}
		} catch (e: Exception) {
			Log.e(tag.error, "Failed to get user info")
		}
		return false
	}

	private suspend fun generateUserData(userWithImage: UserWithImage): Map<String, Any> {
		val deviceID = getFirestoreDeviceID()
		val user = userWithImage.user
		val name = user.name ?: ""
		var imageURL: String = ""
		val tempURL = storeImageAndGetURL(userWithImage.image, user.userID)
		var fileName: String = ""
		val tempFIleName = userWithImage.image.imageName
		if (tempURL != null && tempFIleName != null) {
			imageURL = tempURL
			fileName = tempFIleName
		}
		return mapOf<String, Any>(
			firestoreFields.deviceID to deviceID,
			firestoreFields.isOnline to true,
			firestoreFields.name to name,
			firestoreFields.lastOnline to user.onlineTime,
			firestoreFields.profileImageURL to imageURL,
			firestoreFields.profileImageFileName to fileName,
			firestoreFields.email to user.email,
			firestoreFields.friendList to listOf<String>(),
		)
	}

	private suspend fun storeImageAndGetURL(image: UserImage, userID: String): String? {
		return if (image.imageLocalPath != null && image.imageName != null) {
			cloudStorageFactory.createCloudStorageService(userID)
				.storeProfileImageAndGetURL(
					image.imageName.toString(),
					image.imageLocalPath.toString()
				)
		} else {
			null
		}
	}


	override suspend fun getUserInfo(userID: String): UserImageFirestore? {
		try {
			val data =
				database.collection(firestoreCollections.users)
					.document(userID)
					.get().result
			var name: String? = data[firestoreFields.name].toString()
			if (name == null || name.isEmpty())
				name = null
			val tempImageURL = data[firestoreFields.profileImageURL]?.toString()
			var profileImageFileName: String? = null
			var profileImageURL: String? = null
			val tempFilename =
				data[firestoreFields.profileImageFileName]?.toString()
			if ((tempFilename != null && tempFilename != "") && (tempImageURL != null && tempImageURL != "")) {
				profileImageFileName = tempFilename
				profileImageURL = tempImageURL
			}
			val email = data[firestoreFields.email].toString()
			val isOnline =
				data[firestoreFields.isOnline].toString().toBooleanStrict()
			val lastOnline = data[firestoreFields.lastOnline] as Timestamp
			val friendList =
				data[firestoreFields.friendList].tryCastToStringList<String>()
					?: throw Throwable("Failed to get friend list")

			return UserImageFirestore(
				userID,
				profileImageURL,
				profileImageFileName,
				email,
				name,
				isOnline,
				lastOnline.toDate(),
				friendList
			)
		} catch (e: Exception) {
			return null
		}
	}


	private inline fun <reified T> Any?.tryCastToStringList(): List<T>? {
		@Suppress("UNCHECKED_CAST")
		return if (this is List<*>) {
			return if (this.all { it is T }) {
				this as List<T>
			} else {
				null
			}
		} else {
			null
		}
	}


	override suspend fun sendMessage(
		recipientID: String,
		userID: String,
		message: MessageWithContent
	) {
		val id = "$userID+${GregorianCalendar.getInstance().timeInMillis}"
		val data = generateMessage(userID, message)
		database.collection(firestoreCollections.messages)
			.document(firestoreDocuments.messageData)
			.collection(recipientID.toString())
			.document(id).set(data)
	}

	private suspend fun generateMessage(
		userID: String,
		message: MessageWithContent
	): Map<String, Any> {
		val messageData = message.message
		val media = message.content
		var mediaURL: String = ""
		var mediaFileName: String = ""
		var messageText: String = ""
		if (media.mediaFileName != null && media.mediaLocalPath != null) {
			val mediaContentURL =
				cloudStorageFactory.createCloudStorageService(userID)
					.storeMediaAndGetURL(
						media.mediaFileName.toString(),
						media.mediaLocalPath.toString(),
						media.secondUserIDMedia
					)
			mediaURL = mediaContentURL
			mediaFileName = media.mediaFileName as String
		}
		if (message.text.text != null) {
			messageText = message.text.text as String
		}
		val messageDate = messageData.timestamp ?: throw Throwable("Invalid date")
		val date = DateFormat.getInstance().parse(messageDate)
			?: throw Throwable("Couldn't parse date")
		return mapOf(
			firestoreFields.isRead to false,
			firestoreFields.isRecieved to false,
			firestoreFields.mediaContentURL to mediaURL,
			firestoreFields.mediaFileName to mediaFileName,
			firestoreFields.senderID to userID,
			firestoreFields.sentTime to Timestamp(date),
			firestoreFields.textContent to messageText
		)

	}

	override suspend fun recieveMessages(userID: String): ReceiveChannel<List<MessageWithMedia>> {
		val channel = Channel<List<MessageWithMedia>>()
		val data = database.collection(firestoreCollections.messages)
			.document(firestoreDocuments.messageData).collection(userID)
		data.addSnapshotListener { snapshot, e ->
			if (e != null) {
				Log.e(tag.error, "Document retrieval failed")
				return@addSnapshotListener
			}

			val list = mutableListOf<MessageWithMedia>()
			for (elem in snapshot!!) {
				val tempURL = elem[firestoreFields.mediaContentURL]
				var mediaURL: String? = null
				if (tempURL != null && tempURL != "") {
					mediaURL = tempURL as String
				}
				val tempText = elem[firestoreFields.textContent]
				var textContent: String? = null
				if (tempText != null && tempText != "") {
					textContent = tempText as String
				}
				val isRecieved = elem[firestoreFields.isRecieved].toString()
					.toBooleanStrict()
				val isRead =
					elem[firestoreFields.isRead].toString()
						.toBooleanStrict()
				val sentTime =
					when (val tempTime =
						elem[firestoreFields.sentTime]) {
						is Timestamp -> tempTime.toDate()
						else -> {
							Log.e(
								tag.error,
								"Invalid sent message time"
							)
							continue
						}
					}
				val senderIDLong = elem[firestoreFields.senderID].toString()

				var mediaFileName: String? = null
				val tempFileName = elem[firestoreFields.mediaFileName]
				if (tempFileName != null && tempFileName != "") {
					mediaFileName = tempFileName as String
				}
				val message = MessageWithMedia(
					mediaURL = mediaURL,
					textContent = textContent,
					senderID = senderIDLong,
					sentTime = sentTime,
					isRead = isRead,
					isRecieved = isRecieved,
					mediaFileName = mediaFileName
				)
				list.add(message)
			}
			Log.i(tag.info, "Message List Processed")
			runBlocking {
				Log.i(tag.info, "Sending message list to channel")

				channel.send(list)
			}

		}
		return channel
	}

}