package com.itscryo.hermes.launch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.delay

class launchViewModel(val app: Application) : AndroidViewModel(app) {
	var isLoggedIn: Boolean= false
	private var isInitialized: Boolean= false
	val version:  String=  app.applicationContext.packageManager.getPackageInfo(
		app.applicationContext.packageName,
		0
	).versionName

	suspend fun startupTasks(){

		if(!isInitialized) {
			delay(500)
			//check login status
			isInitialized=true
		}
	}
}