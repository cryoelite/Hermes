package com.itscryo.hermes.app.message.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessageViewModel: ViewModel() {
	val userName = MutableLiveData<String>()

	/**
	 * Stores either Online Status like Online/Available/Busy or Last Online at time.
	 */
	val userOnlineStatus = MutableLiveData<String>()
}