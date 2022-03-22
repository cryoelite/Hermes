package com.itscryo.hermes.global_model

data class FirestoreFields(
	val mediaContentURL: String = "mediaContentURL",
	val textContent: String = "textContent",
	val messageIDIV: String = "messageIDIV",
	val messageIDKey: String = "messageIDKey",
	val deviceID: String = "DeviceID",
	val isOnline: String = "isOnline",
	val lastOnline: String = "lastOnline",
	val name: String = "name",
	val profileImageURL: String = "profileImageURL",
	val email:String= "email",
	val senderID: String="senderID",
	val isRecieved: String= "isRecieved",
	val sentTime: String= "sentTime",
	val isRead:String="isRead",
	val mediaFileName: String= "mediaFileName",
	val profileImageFileName: String="profileImageFileName"
)