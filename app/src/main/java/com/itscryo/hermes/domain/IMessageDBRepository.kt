package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.message_db_model.*
import kotlinx.coroutines.flow.Flow

interface IMessageDBRepository {


	  fun storeUserAsync(user: User): Long

	  fun updateUserAsync(user: User): Int

	  fun getUserAsync(userID: String): Flow<User?>

	  fun getUserFromRowAsync(rowID: Long): Flow<User?>

	  fun deleteUserAsync(user: User): Int


	  fun storeUserImageAsync(userImage: UserImage): Long

	  fun updateUserImageAsync(userImage: UserImage): Int

	  fun getUserImageAsync(imageID: Long): Flow<UserImage?>

	  fun getUserImageFromRowAsync(rowID: Long): Flow<UserImage?>

	  fun deleteUserImageAsync(userImage: UserImage): Int

	  fun getUsersWithImagesAsync(): Flow<List<UserWithImage>?>



	  fun storeMessageAsync(message: Message): Long

	  fun updateMessageAsync(message: Message): Int

	  fun deleteMessageAsync(message: Message): Int

	  fun getMessageAsync(id: Long): Flow<MessageWithContent?>

	  fun getMessageFromRowAsync(rowID: Long): Flow<Message?>

	  fun getMessagesFromIDsAsync(ids: List<Long>): Flow<List<MessageWithContent>?>


	  fun storeMessageTextAsync(messageText: MessageText): Long

	  fun updateMessageTextAsync(messageText: MessageText): Int

	  fun deleteMessageTextAsync(messageText: MessageText): Int

	  fun getMessageTextAsync(id: Long): Flow<MessageText?>

	  fun getMessageTextFromRowAsync(rowID: Long): Flow<MessageText?>



	  fun storeMessageMediaAsync(messageMedia: MessageMedia): Long

	  fun updateMessageMediaAsync(messageMedia: MessageMedia): Int

	  fun deleteMessageMediaAsync(messageMedia: MessageMedia): Int

	  fun getMessageMediaAsync(id: Long): Flow<MessageMedia?>

	  fun getMessageMediaFromRowAsync(rowID: Long): Flow<MessageMedia?>


	  fun getMessagesWithContentAsync(): Flow<List<MessageWithContent>?>


	  fun storeConversationAsync(conversation: Conversation): Long

	  fun updateConversationAsync(conversation: Conversation): Int

	  fun deleteConversationAsync(conversation: Conversation): Int

	  fun getConversationAsync(id: String): Flow<Conversation?>

	  fun getConversationFromRowAsync(rowID: Long): Flow<Conversation?>

	  fun getConversationListAsync(): Flow<List<Conversation>?>

}