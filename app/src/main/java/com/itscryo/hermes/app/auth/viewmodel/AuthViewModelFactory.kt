package com.itscryo.hermes.app.auth.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory() : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
			return AuthViewModel() as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}