package com.itscryo.hermes.global_model.message_db_model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	foreignKeys = arrayOf(
		ForeignKey(
			entity = User::class,
			parentColumns = arrayOf("userID"),
			childColumns = arrayOf("secondUserID"),
			onDelete = ForeignKey.CASCADE
		),  ForeignKey(
			entity = MessageContent::class,
			parentColumns = arrayOf("messageContentID"),
			childColumns = arrayOf("contentID"),
			onDelete = ForeignKey.CASCADE
		)
	),
	indices = arrayOf(
		Index("secondUserID"),
		Index("contentID")
	)
)
data class Conversation(
	@PrimaryKey(autoGenerate = true) var conversationID: Long,
	var secondUserID: Long,
	var contentID: Long?,
	var unreadCount: Int,
)
