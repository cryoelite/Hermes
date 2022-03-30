package com.itscryo.hermes.app.inbox

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.itscryo.hermes.R
import com.itscryo.hermes.app.inbox.model.MessageRecieved
import com.itscryo.hermes.app.inbox.model.MessageSent
import com.itscryo.hermes.app.inbox.model.UserMessageStatus
import com.itscryo.hermes.app.inbox.model.UserMessageStatusEnum
import com.itscryo.hermes.domain.IMessageDBRepository
import com.itscryo.hermes.global_model.message_db_model.MessageWithContent
import com.itscryo.hermes.global_model.message_db_model.UserWithImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import com.itscryo.hermes.app.inbox.model.Message as InboxMessage

class MessageDBToMessage(private val database: IMessageDBRepository, val context: Context) {

	private suspend fun getUsers(): List<UserWithImage>? {
		return database.getUsersWithImagesAsync().first()
	}

	private suspend fun getMessages(ids: List<Long>): List<MessageWithContent>? {
		return database.getMessagesFromIDsAsync(ids).first()
	}

	suspend fun getMessagesFromIDs(ids: List<Long>): List<InboxMessage> {
		val inboxMessageList = mutableListOf<InboxMessage>()
		withContext(Dispatchers.IO) {

			val users = getUsers()
			val messages = getMessages(ids)
			if (messages != null) {
				for (elem in messages) {
					val user = users?.find {
						it.user.userID == elem.message.secondUserID
					} ?: throw Exception("User Not Found")
					val image: Drawable
					if (user.image.imageLocalPath != null) {
						val file = File(user.image.imageLocalPath!!)
						image =
							Glide.with(context)
								.load(file)
								.submit()
								.get()

					} else {
						image = Glide.with(context)
							.load(R.drawable.user_icon).submit().get()
					}

					val inboxMessage: InboxMessage

					if (elem.message.isMessageRecieved) {
						inboxMessage = MessageRecieved(
							userName = user.user.name
								?: user.user.email,
							image = image,
							time = elem.message.timestamp!!,
							message = elem.text.text,
						)

					} else {
						val status = when {
							elem.message.isRead -> UserMessageStatus(
								UserMessageStatusEnum.SEEN
							)
							elem.message.isDelivered -> UserMessageStatus(
								UserMessageStatusEnum.SENT
							)
							else -> UserMessageStatus(
								UserMessageStatusEnum.WAITING
							)
						}

						inboxMessage = MessageSent(
							userName = user.user.name
								?: user.user.email,
							image = image,
							time = elem.message.timestamp!!,
							message = elem.text.text,
							status = status
						)
					}
					inboxMessageList.add(inboxMessage)
				}

			}
		}
		return inboxMessageList
	}

}