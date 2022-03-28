package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.message_db_model.MessageMedia
import com.itscryo.hermes.global_model.message_db_model.UserImage


interface ICloudStorage {
	abstract val userID: String
	suspend fun storeProfileImageAndGetURL(imageName: String, imageLocalPath: String): String
	suspend fun getProfileImageURL(filename: String):String?
	suspend fun  storeMediaAndGetURL(mediaFileName: String, mediaLocalPath: String, secondUserID: String):String
	suspend fun getMediaURL(filename: String):String?

}