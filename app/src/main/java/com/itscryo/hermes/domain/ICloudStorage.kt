package com.itscryo.hermes.domain

interface ICloudStorage {
	suspend fun storeProfileImageAndGetURL(imageLocation: String): String
	suspend fun getProfileImageURL(filename: String):String
}