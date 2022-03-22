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
	override fun storeMessageAsync(
		message: Message,
		messageContent: MessageMedia,
		messateText: MessageText
	)

	@Insert
	override fun storeConversation(conversation: Conversation)

	@Insert
	override fun storeUserAndMessageAsync(
		user: User,
		userImage: UserImage,
		message: Message,
		messageContent: MessageMedia,
		messageText: MessageText
	)

	@Query("Select * FROM User WHERE userID= :userID")
	override fun getUserAsync(userID: Long): Flow<User>

	@Query("Select * FROM UserImage WHERE imageID= :imageID")
	override fun getUserImageAsync(imageID: Long): Flow<UserImage>

	@Query("Select * FROM User,UserImage")
	override fun getUsersWithImagesAsync(): Flow<List<UserWithImage>>

	@Query("Select * FROM Message,MessageMedia,MessageText")
	override fun getMessagesWithContentAsync(): Flow<List<MessageWithContent>>

	@Query("Select * FROM Conversation")
	override fun getConversationsAsync(): Flow<List<Conversation>>

	@Query("Select * FROM Message INNER JOIN MessageMedia ON Message.messageMediaID = MessageMedia.mediaID INNER JOIN MessageText ON Message.messageTextID=MessageText.textID  where messageID IN (:ids)")
	override fun getMessagesFromIDsAsync(ids: List<Long>): Flow<List<MessageWithContent>>

}