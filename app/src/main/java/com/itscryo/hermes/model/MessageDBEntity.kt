package com.itscryo.hermes.model

import android.graphics.drawable.Icon
import java.util.*

data class MessageDBEntity(val messageID: String, val usernameFrom: String, val userNameTo: String, val message: String, val userIcon: Icon, val time: Date,

)