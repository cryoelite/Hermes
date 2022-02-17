package com.itscryo.hermes.domain

import com.itscryo.hermes.model.UserData
import kotlinx.coroutines.Deferred

interface ILocalRepository {
	suspend fun storeUserCred(userData: UserData): Deferred<kotlin.Unit>

}