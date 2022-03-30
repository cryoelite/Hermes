package com.itscryo.hermes.app.launch.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.domain.ILocalRepository

@Suppress("UNCHECKED_CAST")
class launchViewModelFactory(val app: Application, val localRepo: ILocalRepository, val firestoreRepo: IFirestoreRepository) : ViewModelProvider.AndroidViewModelFactory(app) {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(launchViewModel::class.java)) {
			return launchViewModel(app, localRepo, firestoreRepo) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}