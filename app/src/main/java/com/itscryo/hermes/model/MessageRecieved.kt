package com.itscryo.hermes.model

import android.graphics.drawable.Icon

data class MessageRecieved (override val userName: String, override val  image: Icon,override val time: String, val totalMessageRecieved: Int, override val message: String, override val messageType: MessageType= MessageType.RECIEVED_MESSAGE) : Message