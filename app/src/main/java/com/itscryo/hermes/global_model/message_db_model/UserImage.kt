package com.itscryo.hermes.global_model.message_db_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="UserImage")
data class UserImage (@PrimaryKey(autoGenerate = true) var imageID: Long =0L, var imageLocation: String)