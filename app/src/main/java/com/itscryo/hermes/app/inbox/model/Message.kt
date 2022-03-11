package com.itscryo.hermes.app.inbox.model

import android.graphics.drawable.Drawable


enum class MessageType(val index: Int) {
	SENT_MESSAGE(0), RECIEVED_MESSAGE(1),
}


interface Message {
	val messageType: MessageType
	val userName: String
	val message: String?
	val  image: Drawable
	val time: String
}