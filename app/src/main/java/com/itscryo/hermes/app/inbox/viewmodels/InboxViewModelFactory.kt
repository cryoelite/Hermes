package com.itscryo.hermes.app.inbox.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InboxViewModelFactory(val application: Application): ViewModelProvider.Factory {
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(InboxViewModel::class.java)) {
				return InboxViewModel(application) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
}

