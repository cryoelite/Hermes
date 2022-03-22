package com.itscryo.hermes.global_model

import com.itscryo.hermes.BuildConfig

data class SharedPrefKeys(
	val userID: String = "userID",
	val appTheme: String= "appTheme",
	val appLanguage: String= "appLanguage",
	val credFileName: String = "$appID.HermesESPStorage",
	val spFileName: String = "$appID.HermesSPStorage"
) {
	companion object {
		const val appID = BuildConfig.APPLICATION_ID
	}
}
