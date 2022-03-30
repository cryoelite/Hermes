package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.firestore_incoming_model.MessageWithMedia
import com.itscryo.hermes.global_model.message_db_model.MessageWithContent
import com.itscryo.hermes.global_model.message_db_model.UserWithImage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.ReceiveChannel
import com.itscryo.hermes.global_model.firestore_incoming_model.UserWithImage as FirestoreUserImage

interface IFirestoreRepository {

	suspend fun storeUserInfo(user: UserWithImage)

	suspend fun getUserInfo(userID: String): FirestoreUserImage?

	suspend fun isDeviceIDSame(userID: String): Boolean

	suspend fun sendMessage(recipientID: String, userID: String,message: MessageWithContent)

	suspend fun recieveMessages(userID: String): ReceiveChannel<List<MessageWithMedia>>

}