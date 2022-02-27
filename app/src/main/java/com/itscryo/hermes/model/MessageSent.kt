package com.itscryo.hermes.model

import android.graphics.drawable.Icon

data class MessageSent (override val userName: String, override val  image: Icon,override val time: String, val status: UserMessageStatus, override val message: String,  override val messageType: MessageType= MessageType.SENT_MESSAGE): Message