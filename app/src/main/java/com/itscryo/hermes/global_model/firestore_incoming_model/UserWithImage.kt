package com.itscryo.hermes.global_model.firestore_incoming_model

data class UserWithImage(
	val userID: String,
	val profileImageURL: String,
	val profileImageName: String,
	val email: String,
	val name: String?
)
