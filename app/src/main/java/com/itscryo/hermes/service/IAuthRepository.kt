package com.itscryo.hermes.service


import com.itscryo.hermes.model.AuthUserData
import com.itscryo.hermes.model.UserData
import kotlinx.coroutines.Deferred

interface IAuthRepository {
	suspend fun signInAsync(user: AuthUserData): Deferred<UserData>
	suspend fun signUpAsync(user: AuthUserData): Deferred<UserData>
}