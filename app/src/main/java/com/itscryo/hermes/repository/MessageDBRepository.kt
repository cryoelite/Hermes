package com.itscryo.hermes.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.itscryo.hermes.domain.IMessageDBRepository
import com.itscryo.hermes.global_model.message_db_model.*

@Dao
interface MessageDBRepository : IMessageDBRepository {
	@Insert
	override fun storeUserAsync(user: User, userImage: UserImage)

	@Insert
	override fun storeMessageAsync(message: Message, messageContent: MessageContent)

	@Query("Select * FROM User WHERE userID= :userID")
	override fun getUserAsync(userID: Long): User

	@Query("Select * FROM User,UserImage")
	override fun getUsersWithImagesAsync(): LiveData<List<UserWithImage>>

	@Query("Select * FROM Message,MessageContent")
	override fun getMessagesWithContentAsync(): LiveData<List<MessageWithContent>>



}