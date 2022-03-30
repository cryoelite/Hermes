package com.itscryo.hermes.repository

import androidx.room.*
import com.itscryo.hermes.domain.IMessageDBRepository
import com.itscryo.hermes.global_model.message_db_model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDBRepository : IMessageDBRepository {
	@Insert
	override   fun storeUserAsync(user: User): Long

	@Update
	override   fun updateUserAsync(user: User): Int

	@Query("Select * FROM User WHERE userID= :userID")
	override   fun getUserAsync(userID: String): Flow<User?>

	@Delete
	override   fun deleteUserAsync(user: User): Int

	@Query("Select * FROM User WHERE rowID= :rowID")
	override   fun getUserFromRowAsync(rowID: Long): Flow<User?>



	@Insert
	override   fun storeUserImageAsync(userImage: UserImage): Long

	@Query("Select * FROM UserImage WHERE imageID= :imageID")
	override   fun getUserImageAsync(imageID: Long): Flow<UserImage?>

	@Query("Select * FROM UserImage WHERE rowID= :rowID")
	override   fun getUserImageFromRowAsync(rowID: Long): Flow<UserImage?>

	@Update
	override   fun updateUserImageAsync(userImage: UserImage): Int

	@Delete
	override   fun deleteUserImageAsync(userImage: UserImage): Int

	@Query("Select * FROM User,UserImage")
	override   fun getUsersWithImagesAsync(): Flow<List<UserWithImage>?>


	@Insert
	override   fun storeMessageAsync(message: Message): Long

	@Update
	override   fun updateMessageAsync(message: Message): Int

	@Delete
	override   fun deleteMessageAsync(message: Message): Int

	@Query("Select * FROM Message INNER JOIN MessageMedia ON Message.messageMediaID = MessageMedia.mediaID INNER JOIN MessageText ON Message.messageTextID=MessageText.textID  where messageID = :id")
	override   fun getMessageAsync(id: Long): Flow<MessageWithContent?>

	@Query("Select * FROM Message INNER JOIN MessageMedia ON Message.messageMediaID = MessageMedia.mediaID INNER JOIN MessageText ON Message.messageTextID=MessageText.textID  where messageID IN (:ids)")
	override   fun getMessagesFromIDsAsync(ids: List<Long>): Flow<List<MessageWithContent>?>

	@Query("Select * FROM Message WHERE rowID= :rowID")
	override   fun getMessageFromRowAsync(rowID: Long): Flow<Message?>


	@Insert
	override   fun storeMessageTextAsync(messageText: MessageText): Long

	@Update
	override   fun updateMessageTextAsync(messageText: MessageText): Int

	@Delete
	override   fun deleteMessageTextAsync(messageText: MessageText): Int

	@Query("Select *  FROM MessageText WHERE textID=:id")
	override   fun getMessageTextAsync(id: Long): Flow<MessageText?>

	@Query("Select * FROM MessageText WHERE rowID= :rowID")
	override   fun getMessageTextFromRowAsync(rowID: Long): Flow<MessageText?>



	@Insert
	override   fun storeMessageMediaAsync(messageMedia: MessageMedia): Long

	@Update
	override   fun updateMessageMediaAsync(messageMedia: MessageMedia): Int

	@Delete
	override   fun deleteMessageMediaAsync(messageMedia: MessageMedia): Int

	@Query("Select * FROM MessageMedia where mediaID= :id")
	override   fun getMessageMediaAsync(id: Long): Flow<MessageMedia?>

	@Query("Select * FROM Message,MessageMedia,MessageText")
	override   fun getMessagesWithContentAsync(): Flow<List<MessageWithContent>?>

	@Query("Select * FROM MessageMedia WHERE rowID= :rowID")
 	override   fun getMessageMediaFromRowAsync(rowID: Long): Flow<MessageMedia?>



	@Insert
	override   fun storeConversationAsync(conversation: Conversation): Long

	@Update
	override   fun updateConversationAsync(conversation: Conversation): Int

	@Query("Select * FROM Conversation")
	override   fun getConversationListAsync(): Flow<List<Conversation>?>

	@Query("Select * FROM Conversation WHERE rowID= :rowID")
        override    fun getConversationFromRowAsync(rowID: Long): Flow<Conversation?>


	@Delete
	override   fun deleteConversationAsync(conversation: Conversation): Int

	@Query("Select * FROM Conversation where secondUserID= :id")
	override   fun getConversationAsync(id: String): Flow<Conversation?>

}