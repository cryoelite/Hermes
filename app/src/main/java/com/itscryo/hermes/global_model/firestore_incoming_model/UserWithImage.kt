package com.itscryo.hermes.global_model.firestore_incoming_model

data class UserWithImage(
	val userID: Long,
	val profileImageURL: String,
	val email: String,
	val name: String?
)
