package com.itscryo.hermes.global_model.message_db_model

import androidx.room.*
import java.util.*

@Entity(
	foreignKeys = [ForeignKey(
		entity = UserImage::class,
		parentColumns = arrayOf("imageID"),
		childColumns = arrayOf("userImageID"),
		onDelete = ForeignKey.CASCADE
	)],
	indices = [Index("userImageID")]
)
data class User(
	@PrimaryKey(autoGenerate = false) var userID: String,
	var name: String?,
	var userImageID: Long,
	var email: String,
	var isOnline: Boolean,
	@ColumnInfo(name = "onlineTime", defaultValue = "(datetime('now'))")
	var userOnlineTime: String?= null
) {

	@Ignore
	var onlineTime: Date = GregorianCalendar.getInstance().time
		set(value) {
			field = value
			userOnlineTime = value.toString()
		}
}