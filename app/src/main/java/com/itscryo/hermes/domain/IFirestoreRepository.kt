package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.message_db_model.MessageWithContent
import com.itscryo.hermes.global_model.message_db_model.UserWithImage

interface IFirestoreRepository {

	fun storeUserInfo(user: UserWithImage)

	fun getUserInfo(userID: Long): UserWithImage

	fun sendMessage(recipientID: Long, message: MessageWithContent)

	fun recieveMessages(): List<MessageWithContent>
}