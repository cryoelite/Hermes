package com.itscryo.hermes.domain

import androidx.lifecycle.LiveData
import com.itscryo.hermes.global_model.message_db_model.*

interface IMessageDBRepository {
	fun storeUserAsync(user: User, userImage: UserImage)

	fun storeMessageAsync(message: Message, messageContent: MessageContent)

	fun getUserAsync(userID: Long): User

	fun getUsersWithImagesAsync(): LiveData<List<UserWithImage>>

	fun getMessagesWithContentAsync(): LiveData<List<MessageWithContent>>

}