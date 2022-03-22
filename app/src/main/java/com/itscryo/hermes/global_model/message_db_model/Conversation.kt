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
	indices = [Index("secondUserID"), Index("messageMediaID"), Index("messageTextID")], tableName = "Conversation"
)
data class Conversation(
	@PrimaryKey(autoGenerate = false) var secondUserID: String,
	var messageMediaID: Long,
	var messageTextID: Long,
	var unreadCount: Int,
)
