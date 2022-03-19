package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.message_db_model.*
import kotlinx.coroutines.flow.Flow

interface IMessageDBRepository {
	fun storeUserAsync(user: User, userImage: UserImage)

	fun storeMessageAsync(message: Message, messageContent: MessageContent)

	fun storeUserAndMessageAsync(
		user: User,
		userImage: UserImage,
		message: Message,
		messageContent: MessageContent
	)

	fun storeUserImage(userImage: UserImage)

	fun storeConversation(conversation: Conversation)

	fun getUserAsync(userID: Long): Flow<User>

	fun getUsersWithImagesAsync(): Flow<List<UserWithImage>>

	fun getMessagesWithContentAsync(): Flow<List<MessageWithContent>>

	fun getConversationsAsync(): Flow<List<Conversation>>

	fun getMessagesFromIDsAsync(ids: List<Long>): Flow<List<MessageWithContent>>


}