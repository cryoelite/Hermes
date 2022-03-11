package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.UserData
import com.itscryo.hermes.global_model.UserPreferences
import kotlinx.coroutines.Deferred

interface ILocalRepository {
	suspend fun storeUserCredAsync(userData: UserData): Deferred<kotlin.Unit>

	suspend fun storePrefsAsync(userPrefs: UserPreferences): Deferred<kotlin.Unit>


}