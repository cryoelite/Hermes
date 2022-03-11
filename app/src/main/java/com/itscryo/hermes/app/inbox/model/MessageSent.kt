package com.itscryo.hermes.app.inbox.model

import android.graphics.drawable.Drawable

data class MessageSent (override val userName: String, override val  image: Drawable, override val time: String, val status: UserMessageStatus, override val message: String?, override val messageType: MessageType= MessageType.SENT_MESSAGE): Message