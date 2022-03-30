package com.itscryo.hermes.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.itscryo.hermes.global_model.message_db_model.User
import com.itscryo.hermes.global_model.message_db_model.UserImage
import com.itscryo.hermes.repository.MessageDBRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.itscryo.hermes.global_model.firestore_incoming_model.UserWithImage as FirestoreUserWithImage

@AndroidEntryPoint
class MessageDBService() : Service() {

	private val localBinder = LocalBinder()

	@Inject
	lateinit var db: MessageDBRepository


	private lateinit var mediaServiceBinder: MediaService.LocalBinder
	private var isMediaServiceBound = false

	private val connection = object : ServiceConnection {
		override fun onServiceConnected(className: ComponentName, service: IBinder) {
			mediaServiceBinder = service as MediaService.LocalBinder
			isMediaServiceBound = true
		}

		override fun onServiceDisconnected(arg0: ComponentName) {
			isMediaServiceBound = false
		}

	}

	private suspend fun <T> withBind(lambda: suspend () -> T): T {
		bind()
		val result = lambda()
		unbind()
		return result
	}

	private fun bind() {
		Intent(this.applicationContext, MediaService::class.java).also { intent ->
			this.applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
		}
	}

	private fun unbind() {
		this.applicationContext.unbindService(connection)
	}

	inner class LocalBinder : Binder() {
		fun getService() = this@MessageDBService
	}

	suspend fun generateUser(email: String, userID: String) {
		val image = UserImage(
			imageLocalPath = null,
			imageName = null,
		)
		val imageRow = db.storeUserImageAsync(image)
		val imageDB = db.getUserImageFromRowAsync(imageRow).first()
		if (imageDB?.imageID == null) {
			throw Throwable("Failed to get user image")
		}

		val user = User(
			userID,
			isOnline = true,
			email = email,
			userImageID = imageDB.imageID as Long,
			name = null,
		)
		db.storeUserAsync(user)
	}

	suspend fun storeFirestoreUser(userWithImage: FirestoreUserWithImage) {
		val userInfo = db.getUserAsync(userWithImage.userID).first()
		var profileImageLocalPath: String? = null
		var imageName: String? = null
		if (userWithImage.profileImageURL != null && userWithImage.profileImageName != null) {
			imageName = userWithImage.profileImageName
			profileImageLocalPath = withBind<String> {
				return@withBind mediaServiceBinder.storeImageAndGetLocalPath(
					userWithImage.profileImageURL,
					userWithImage.profileImageName,
				)
			}
		}
		val userImage = UserImage(
			imageLocalPath = profileImageLocalPath,
			imageName = imageName
		)
		if (userInfo != null) {
			userImage.imageID = userInfo.userImageID
			val user = User(
				userWithImage.userID,
				userWithImage.name,
				userInfo.userImageID,
				email = userWithImage.email,
				isOnline = userWithImage.isOnline,
			)
			user.onlineTime = userWithImage.lastOnline
			db.updateUserImageAsync(userImage)
			db.updateUserAsync(user)
		} else {
			userImage.imageID = null
			val imageRowID = db.storeUserImageAsync(userImage)
			val dbUserImage = db.getUserImageFromRowAsync(imageRowID).first()
			if (dbUserImage?.imageID == null) {
				throw Throwable("Failed to get stored user image")
			}
			val user = User(
				userWithImage.userID,
				userWithImage.name,
				dbUserImage.imageID as Long,
				email = userWithImage.email,
				isOnline = userWithImage.isOnline,

			)
			user.onlineTime=userWithImage.lastOnline
			db.storeUserAsync(user)
		}
	}


	override fun onBind(intent: Intent?): IBinder {
		return localBinder
	}

	override fun onDestroy() {
		if (isMediaServiceBound) {
			unbind()
		}
		super.onDestroy()
	}
}