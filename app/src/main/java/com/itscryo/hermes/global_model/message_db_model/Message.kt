package com.itscryo.hermes.global_model.message_db_model

import androidx.room.*

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
data class Message(
	@PrimaryKey(autoGenerate = true) var messageID: Long,
	var secondUserID: Long,
	var contentID: Long,
	@ColumnInfo(defaultValue = "(datetime('now'))") var timestamp: String,
	var isRead: Boolean,
	var isMessageRecieved: Boolean,
	var isDelivered: Boolean
)