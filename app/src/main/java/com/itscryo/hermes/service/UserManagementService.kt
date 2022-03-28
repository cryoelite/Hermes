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
import com.itscryo.hermes.global_model.message_db_model.UserWithImage
import com.itscryo.hermes.repository.MessageDBRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class UserManagementService(@ApplicationContext val context: Context) : Service() {
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
		Intent(context, MediaService::class.java).also { intent ->
			context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
		}
	}

	private fun unbind() {
		context.unbindService(connection)
	}

	inner class LocalBinder : Binder() {
		fun getService() = this@UserManagementService
	}


	suspend fun firestoreUserToLocalUser(userWithImage: com.itscryo.hermes.global_model.firestore_incoming_model.UserWithImage): UserWithImage {
		val userInfo = db.getUserAsync(userWithImage.userID).first()
		if (userInfo != null) {
			var profileImageLocalPath: String? = null
			var imageName: String? = null
			if (userWithImage.profileImageURL != null && userWithImage.profileImageName != null) {
				imageName = userWithImage.profileImageName
				profileImageLocalPath =withBind<String> {
					return@withBind mediaServiceBinder.storeImageAndGetLocalPath(
						userWithImage.profileImageURL
					)
				}
			}
			val userImage = UserImage(
				imageID = userInfo.userImageID,
				imageLocalPath = profileImageLocalPath,
				imageName = imageName
			)
			val user = User(
				userWithImage.userID,
				userWithImage.name,
				userInfo.userImageID,
				email = userWithImage.email,
				isOnline = userWithImage.isOnline,
				userWithImage.lastOnline.toString(),
			)
		}

	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onDestroy() {
		if(isMediaServiceBound) {
			unbind()
		}
		super.onDestroy()
	}
}