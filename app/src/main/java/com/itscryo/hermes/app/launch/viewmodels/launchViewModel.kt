package com.itscryo.hermes.app.launch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.domain.ILocalRepository
import kotlinx.coroutines.delay

class launchViewModel(app: Application, val localRepo: ILocalRepository, val firestoreRepo: IFirestoreRepository) : AndroidViewModel(app) {
	var isLoggedIn: Boolean= false
	private var isInitialized: Boolean= false
	val version:  String=  app.applicationContext.packageManager.getPackageInfo(
		app.applicationContext.packageName,
		0
	).versionName

	suspend fun startupTasks(){

		if(!isInitialized) {
			val userCred= localRepo.retrieveUserCredAsync(
				getApplication<Application>().applicationContext
			)
			if(userCred!=null){
				val firestoreUser= firestoreRepo.getUserInfo(userCred)
				if(firestoreUser!=null) {
					val result= firestoreRepo.isDeviceIDSame(userCred)
					if(result)
					{
						isLoggedIn=true
					}
				}
			}
			isInitialized=true
		}
	}
}