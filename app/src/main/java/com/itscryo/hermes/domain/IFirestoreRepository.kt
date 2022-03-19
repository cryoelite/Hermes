package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.firestore_incoming_model.MessageWithMedia
import com.itscryo.hermes.global_model.message_db_model.MessageWithContent
import com.itscryo.hermes.global_model.message_db_model.UserWithImage
import kotlinx.coroutines.channels.ReceiveChannel
import com.itscryo.hermes.global_model.firestore_incoming_model.UserWithImage as UserImageFirestore

interface IFirestoreRepository {

	suspend fun storeUserInfo(user: UserWithImage)

	suspend fun getUserInfo(userID: Long): UserImageFirestore

	suspend fun sendMessage(recipientID: Long, userID: Long,message: MessageWithContent)

	suspend fun recieveMessages(userID: Long): ReceiveChannel<List<MessageWithMedia>>

}