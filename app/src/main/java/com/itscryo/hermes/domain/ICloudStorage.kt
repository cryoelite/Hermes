package com.itscryo.hermes.domain

import com.itscryo.hermes.global_model.message_db_model.MessageMedia
import com.itscryo.hermes.global_model.message_db_model.UserImage
import com.itscryo.hermes.global_model.firestore_incoming_model.UserImage as FirestoreUserImage
import com.itscryo.hermes.global_model.firestore_incoming_model.MessageMedia as FirestoreMedia


interface ICloudStorage {
	abstract val userID: String
	suspend fun storeProfileImageAndGetURL(userImage: UserImage): String
	suspend fun getProfileImage(filename: String):FirestoreUserImage?
	suspend fun  storeMediaAndGetURL(media: MessageMedia):String
	suspend fun getMedia(filename: String):FirestoreMedia?

}