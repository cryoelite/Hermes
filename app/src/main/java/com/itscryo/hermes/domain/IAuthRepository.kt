package com.itscryo.hermes.domain


import com.itscryo.hermes.global_model.AuthUserData
import com.itscryo.hermes.global_model.UserData
import kotlinx.coroutines.Deferred

interface IAuthRepository {
	suspend fun signInAsync(user: AuthUserData): Deferred<UserData>
	suspend fun signUpAsync(user: AuthUserData): Deferred<UserData>

}