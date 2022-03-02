package com.itscryo.hermes.app.inbox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MessageViewModelFactory : ViewModelProvider.Factory {
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(MessageModel::class.java)) {
				return MessageModel() as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
}

