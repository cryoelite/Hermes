package com.itscryo.hermes.global_model.message_db_model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	foreignKeys = arrayOf(
		ForeignKey(
			entity = UserImage::class,
			parentColumns = arrayOf("imageID"),
			childColumns = arrayOf("userImageID"),
			onDelete = ForeignKey.CASCADE
		)
	),
	indices = arrayOf(
		Index("userImageID"),
	)
)
data class User(
	@PrimaryKey(autoGenerate = false) var userID: Long,
	var name: String?,
	var userImageID: Long,
	var email: String
)