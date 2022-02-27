package com.itscryo.hermes.model

import android.graphics.drawable.Icon

enum class UserMessageStatusEnum {
	SENT, SEEN, WAITING,
}

data class UserMessageStatus(val status: UserMessageStatusEnum, val icon: Icon )