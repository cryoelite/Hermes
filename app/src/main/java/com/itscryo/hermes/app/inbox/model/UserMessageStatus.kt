package com.itscryo.hermes.app.inbox.model

import android.graphics.drawable.Drawable
import com.itscryo.hermes.R

enum class UserMessageStatusEnum(val drawableID: Int) {
	SENT(R.drawable.message_delivered_icon), SEEN(R.drawable.message_read_icon), WAITING(R.drawable.message_waiting_icon),
}

data class UserMessageStatus(val status: UserMessageStatusEnum )