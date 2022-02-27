package com.itscryo.hermes.model

import android.graphics.drawable.Icon

enum class MessageType(val index: Int) {
	SENT_MESSAGE(0), RECIEVED_MESSAGE(1),
}


interface Message {
	val messageType: MessageType
	val userName: String
	val message: String
	val  image: Icon
	val time: String
}