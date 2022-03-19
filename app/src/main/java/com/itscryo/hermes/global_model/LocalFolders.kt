package com.itscryo.hermes.global_model

import android.os.Environment

data class LocalFolders(val profilePictureFolder:String= "$baseDir/Media/Profile Pictures/") {
	companion object{
		val baseDir: String= Environment.getDataDirectory().toString()
	}
}
