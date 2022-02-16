package com.itscryo.hermes.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itscryo.hermes.launch.launchViewModel

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory() : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
			return AuthViewModel() as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}