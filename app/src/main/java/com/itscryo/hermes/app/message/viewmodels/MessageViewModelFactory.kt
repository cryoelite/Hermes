package com.itscryo.hermes.app.message.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MessageViewModelFactory(): ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
			return MessageViewModel() as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
