package com.itscryo.hermes.global_model.message_db_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageContent(@PrimaryKey(autoGenerate = true) var messageContentID: Long, var messageContent: String?, var mediaLocation: String?)