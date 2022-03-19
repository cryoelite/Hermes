package com.itscryo.hermes.global_model.firestore_incoming_model

data class MessageWithMedia(val mediaContentLocation: String, val textContent: String, val senderID: Long)
