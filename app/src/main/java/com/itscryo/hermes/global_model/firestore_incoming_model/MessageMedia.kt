package com.itscryo.hermes.global_model.firestore_incoming_model

data class MessageMedia(
	val mediaURL: String,
	val mediaFileName: String,
	val secondUserID: Long,
)
