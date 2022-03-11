package com.itscryo.hermes.app.inbox

import android.content.Context
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.itscryo.hermes.app.inbox.model.MessageRecieved
import com.itscryo.hermes.app.inbox.model.MessageSent
import com.itscryo.hermes.app.inbox.model.UserMessageStatus
import com.itscryo.hermes.app.inbox.model.UserMessageStatusEnum
import com.itscryo.hermes.global_model.message_db_model.*
import com.itscryo.hermes.service.MessageDatabase
import java.io.File
import com.itscryo.hermes.app.inbox.model.Message as InboxMessage

class MessageDBToMessage(val database: MessageDatabase, val context: Context) {

	fun getUserInfo(userList: List<UserWithImage>, id: Long): UserWithImage{
		val result=userList.find { it.user.userID ==  id}

	}

	companion object {
		fun MessageDBToMessage.from(message: LiveData<MessageWithContent>, user: LiveData<List<UserWithImage>>): LiveData<InboxMessage> {
			val inboxMessage: InboxMessage;
			val user = getUserInfo(message.message.secondUserID)
			val userImage = getUserImage(user.userImageID)
			val content = getMessageBody(message.messageContentID)
			val image = Glide.with(context).load(File(userImage.imageLocation)).submit()
				.get()
			if (message.isMessageRecieved) {
				inboxMessage = MessageRecieved(
					userName = user.name ?: user.email,
					image = image,
					time = message.timestamp,
					message = content.messageContent,
				)

			} else {
				val status = when {
					message.isRead -> UserMessageStatus(UserMessageStatusEnum.SEEN)
					message.isDelivered -> UserMessageStatus(
						UserMessageStatusEnum.SENT
					)
					else -> UserMessageStatus(UserMessageStatusEnum.WAITING)
				}

				inboxMessage = MessageSent(
					userName = user.name ?: user.email,
					image = image,
					time = message.timestamp,
					message = content.messageContent,
					status = status
				)
			}
			return inboxMessage
		}
	}
}