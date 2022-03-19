package com.itscryo.hermes.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.itscryo.hermes.domain.IMessageDBRepository
import com.itscryo.hermes.global_model.message_db_model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDBRepository : IMessageDBRepository {
	@Insert
	override fun storeUserAsync(user: User, userImage: UserImage)

	@Insert
	override fun storeUserImage(userImage: UserImage)


	@Insert
	override fun storeMessageAsync(message: Message, messageContent: MessageContent)

	@Insert
	override fun storeConversation(conversation: Conversation)

	@Insert
	override fun storeUserAndMessageAsync(
		user: User,
		userImage: UserImage,
		message: Message,
		messageContent: MessageContent
	)

	@Query("Select * FROM User WHERE userID= :userID")
	override fun getUserAsync(userID: Long): Flow<User>

	@Query("Select * FROM User,UserImage")
	override fun getUsersWithImagesAsync(): Flow<List<UserWithImage>>

	@Query("Select * FROM Message,MessageContent")
	override fun getMessagesWithContentAsync(): Flow<List<MessageWithContent>>

	@Query("Select * FROM Conversation")
	override fun getConversationsAsync(): Flow<List<Conversation>>

	@Query("Select * FROM Message INNER JOIN MessageContent ON Message.contentID = MessageContent.messageContentID where messageID IN (:ids)")
	override fun getMessagesFromIDsAsync(ids: List<Long>): Flow<List<MessageWithContent>>

}