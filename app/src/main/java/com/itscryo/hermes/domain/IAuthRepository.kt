package com.itscryo.hermes.domain



import kotlinx.coroutines.Deferred

interface IAuthRepository {
	suspend fun signInAsync(email: String, pass: String): Deferred<String>
	suspend fun signUpAsync(email: String, pass: String): Deferred<String>

}