package com.itscryo.hermes.app.launch.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
@Suppress("UNCHECKED_CAST")
class launchViewModelFactory(val app: Application) : ViewModelProvider.AndroidViewModelFactory(app) {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(launchViewModel::class.java)) {
			return launchViewModel(app) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}