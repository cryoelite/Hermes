package com.itscryo.hermes.app.inbox.model

import android.graphics.drawable.Drawable

enum class UserMessageStatusEnum {
	SENT, SEEN, WAITING,
}

data class UserMessageStatus(val status: UserMessageStatusEnum, val icon: Drawable )