package com.itscryo.hermes.app.inbox.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.itscryo.hermes.app.inbox.MessageDBToMessage
import com.itscryo.hermes.app.inbox.model.Message
import com.itscryo.hermes.global_model.message_db_model.Conversation
import com.itscryo.hermes.service.MessageDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MessageModel(application: Application) : AndroidViewModel(application) {
	lateinit var messageDBToMessage: MessageDBToMessage

	lateinit var messageList: Flow<List<Message>>

	private lateinit var conversationList: Flow<List<Conversation>>


	private suspend fun getMessageIDs(conversation: List<Conversation>): List<Long> {
		val list = conversation.map {
			it.messageTextID
		}
		return list;
	}

	suspend fun initAsync() {
		val db= MessageDatabase.getInstance(getApplication<Application>().applicationContext)
		if (!::messageList.isInitialized) {
			messageDBToMessage = MessageDBToMessage(
				db,
				getApplication<Application>().applicationContext
			)
			conversationList = db.messageDBRepository.getConversationsAsync()
			conversationList.collect {
				viewModelScope.launch {
					val messageIDs = getMessageIDs(it)
					val messages =
						messageDBToMessage.getMessagesFromIDs(messageIDs)
					messageList = flowOf(messages)
				}
			}

		}
	}

	override fun onCleared() {
		super.onCleared()
	}

}