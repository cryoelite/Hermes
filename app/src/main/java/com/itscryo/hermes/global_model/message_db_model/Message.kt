package com.itscryo.hermes.global_model.message_db_model

import androidx.room.*

@Entity(
	foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = arrayOf("userID"),
		childColumns = arrayOf("secondUserID"),
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = MessageMedia::class,
		parentColumns = arrayOf("mediaID"),
		childColumns = arrayOf("messageMediaID"),
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = MessageText::class,
		parentColumns = arrayOf("textID"),
		childColumns = arrayOf("messageTextID"),
		onDelete = ForeignKey.CASCADE
	)],
	indices = [Index("secondUserID"), Index("messageMediaID", unique = true), Index("messageTextID", unique = true)]
)
data class Message(
	@PrimaryKey(autoGenerate = false) var messageID: Long,
	var secondUserID: String,
	var messageMediaID: Long,
	val messageTextID: Long,
	@ColumnInfo(defaultValue = "(datetime('now'))") var timestamp: String?,
	var isRead: Boolean,
	var isMessageRecieved: Boolean,
	var isDelivered: Boolean
)