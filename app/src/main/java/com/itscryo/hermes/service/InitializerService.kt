package com.itscryo.hermes.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.domain.IMessageDBRepository
import com.itscryo.hermes.global_model.IntentKeys
import com.itscryo.hermes.global_model.LogTags
import com.itscryo.hermes.global_model.firestore_incoming_model.UserWithImage
import com.itscryo.hermes.global_model.message_db_model.User
import com.itscryo.hermes.global_model.message_db_model.UserImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.itscryo.hermes.global_model.message_db_model.UserWithImage as LocalUserWithImage


/**
 * Service to initialize the database and synchronize the local db with the firebase
 */
@AndroidEntryPoint
class InitializerService : Service() {
	private val job = SupervisorJob()
	private val scope = CoroutineScope(Dispatchers.IO + job)


	@Inject
	lateinit var db: IMessageDBRepository

	@Inject
	lateinit var localRepo: ILocalRepository

	@Inject
	lateinit var firestoreRepo: IFirestoreRepository

	val tag = LogTags("Init Service")

	val intentKeys = IntentKeys()
	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		scope.launch {
			val userID = localRepo.retrieveUserCredAsync()
			if (userID == null) {
				Log.i(
					tag.info,
					"User ID doesn't exist in local repo, stopping service"
				)
				stopSelf(startId)
			} else {
				setupUsers(userID)
			}


		}
		return START_REDELIVER_INTENT
	}

	private suspend fun storeUser(userWithImage: UserWithImage): LocalUserWithImage {
		val userInfo = db.getUserAsync(userWithImage.userID).first()
		if (userInfo != null) {
			var profileImageLocalPath: String? = null
			var imageName: String? = null
			if (userWithImage.profileImageURL != null && userWithImage.profileImageName != null) {
				imageName = userWithImage.profileImageName
				profileImageLocalPath =
					localRepo.storeImageFromURLAsync(userWithImage.profileImageURL)
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

	private suspend fun setupUsers(userID: String) {
		val userInfo = firestoreRepo.getUserInfo(userID)
		if (userInfo == null) {
			Log.e(tag.error, "User info doesn't exist in the firestore")
			return
		} else {
			val friendListString = userInfo.friendList
			val friendList = mutableListOf<UserWithImage>()
			for (elem in friendListString) {
				val tempUser = firestoreRepo.getUserInfo(elem) ?: continue

			}

		}

	}

	override fun onDestroy() {
		job.cancel()
		super.onDestroy()
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}
}
