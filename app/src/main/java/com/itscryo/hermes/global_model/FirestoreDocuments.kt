package com.itscryo.hermes.global_model

import com.itscryo.hermes.service.GlobalEncryption
import java.util.*

data class FirestoreDocuments(
	val messageData: String = "Data",
	val appProperties: String = "App",
)