package com.itscryo.hermes.app.inbox.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itscryo.hermes.app.inbox.model.Message

class MessageModel: ViewModel() {
	private val _messageList = MutableLiveData<List<Message>>()

	val messageList: LiveData<List<Message>>
		get() = _messageList
}