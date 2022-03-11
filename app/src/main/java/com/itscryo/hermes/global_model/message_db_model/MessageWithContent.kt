package com.itscryo.hermes.global_model.message_db_model

import androidx.room.Embedded

data class MessageWithContent(@Embedded val message: Message,@Embedded val content: MessageContent)