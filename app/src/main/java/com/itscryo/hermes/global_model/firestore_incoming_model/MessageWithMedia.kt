package com.itscryo.hermes.global_model.firestore_incoming_model

import java.util.*

data class MessageWithMedia(val mediaURL: String?, val textContent: String?, val senderID: String, val sentTime: Date, val isRead: Boolean, val isRecieved: Boolean, val mediaFileName: String?)
