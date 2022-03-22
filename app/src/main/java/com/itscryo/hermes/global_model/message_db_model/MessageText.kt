package com.itscryo.hermes.global_model.message_db_model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = arrayOf("userID"),
			childColumns = arrayOf("secondUserIDText"),
			onDelete = ForeignKey.CASCADE
		),
	], indices = [Index("secondUserIDText")]
)
data class MessageText(
	@PrimaryKey(autoGenerate = true) var textID: Long? =null,
	var text: String?,
	var secondUserIDText: String,
)
