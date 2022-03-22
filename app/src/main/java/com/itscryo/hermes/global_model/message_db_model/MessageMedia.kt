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
			childColumns = arrayOf("secondUserIDMedia"),
			onDelete = ForeignKey.CASCADE
		),
	], indices = [Index("secondUserIDMedia")]
)
data class MessageMedia(
	@PrimaryKey(autoGenerate = true) var mediaID: Long?=null,
	var mediaLocalPath: String?,
	var mediaFileName: String?,
	var secondUserIDMedia: String,
/**Media is an image, for it to be another type, we'd require a Media Holder class that defines the metadata for media along with its location. Considering the scope of this project, I choose it to be this simple.
 *
 *  Update: Solved */
)