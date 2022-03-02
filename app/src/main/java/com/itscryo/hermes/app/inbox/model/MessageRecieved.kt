package com.itscryo.hermes.app.inbox.model

import android.graphics.drawable.Drawable

data class MessageRecieved (override val userName: String, override val  image: Drawable, override val time: String, val totalMessageRecieved: Int, override val message: String, override val messageType: MessageType= MessageType.RECIEVED_MESSAGE) : Message