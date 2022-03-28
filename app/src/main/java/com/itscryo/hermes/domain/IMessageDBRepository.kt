package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.message_db_model.*
import kotlinx.coroutines.flow.Flow

interface IMessageDBRepository {
	fun storeUserAsync(user: User)

	fun storeUserImageAsync(userImage: UserImage)

	fun updateUserAsync(user:User)

	fun updateUserImage(userImage: UserImage)

	fun storeMessageAsync(
		message: Message,
		messageContent: MessageMedia,
		messateText: MessageText
	)

	fun storeUserAndMessageAsync(
		user: User,
		userImage: UserImage,
		message: Message,
		messageContent: MessageMedia,
		messageText: MessageText
	)


	fun storeConversation(conversation: Conversation)

	fun getUserAsync(userID: String): Flow<User?>

	fun getUserImageAsync(imageID: Long): Flow<UserImage?>


	fun getUsersWithImagesAsync(): Flow<List<UserWithImage>>

	fun getMessagesWithContentAsync(): Flow<List<MessageWithContent>>

	fun getConversationsAsync(): Flow<List<Conversation>>

	fun getMessagesFromIDsAsync(ids: List<Long>): Flow<List<MessageWithContent>>

//TODO: Check behavior when a user is requested but it doesn't exist in db
}