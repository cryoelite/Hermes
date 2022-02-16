package com.itscryo.hermes.service

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.itscryo.hermes.model.AuthUserData
import com.itscryo.hermes.model.UserData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred


class FirebaseAuthRepository private constructor(private val firebaseAuth: FirebaseAuth) :
	IAuthRepository {
	companion object {
		fun create(): FirebaseAuthRepository {
			return FirebaseAuthRepository(Firebase.auth);
		}
	}

	override suspend fun signInAsync(user: AuthUserData): Deferred<UserData> {
		val deferred = CompletableDeferred<UserData>()

		try {
			val result =
				firebaseAuth.signInWithEmailAndPassword(user.email, user.pass)
					.asDeferredAsync().await()
			if (result.user == null) {
				deferred.completeExceptionally(Exception("Unknown error in server"))
			} else {
				deferred.complete(UserData(result.user!!.uid))
			}
		} catch (e: FirebaseAuthInvalidCredentialsException) {
			deferred.completeExceptionally(Exception("Invalid Password"))
		} catch (e: FirebaseAuthInvalidUserException) {
			return signUpAsync(user)
		} catch (e: FirebaseAuthException) {
			deferred.completeExceptionally(
				Exception(
					e.localizedMessage
				)
			)
		} catch (e: FirebaseNetworkException) {
			deferred.completeExceptionally(
				Exception(
					"Cannot connect to servers"
				)
			)
		}
		return deferred

	}

	override suspend fun signUpAsync(user: AuthUserData): Deferred<UserData> {
		val deferred = CompletableDeferred<UserData>()

		try {
			val result =
				firebaseAuth.createUserWithEmailAndPassword(user.email, user.pass)
					.asDeferredAsync().await()
			if (result.user == null) {
				deferred.completeExceptionally(Exception("Unknown error in server"))
			} else {
				deferred.complete(UserData(result.user!!.uid))
			}
		} catch (e: FirebaseAuthWeakPasswordException) {
			deferred.completeExceptionally(Exception(e.message))

		} catch (e: FirebaseAuthException) {
			deferred.completeExceptionally(
				Exception(
					e.localizedMessage
				)
			)
		}
		return deferred
	}
}
