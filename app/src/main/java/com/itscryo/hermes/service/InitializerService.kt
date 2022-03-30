package com.itscryo.hermes.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.itscryo.hermes.domain.IFirestoreRepository
import com.itscryo.hermes.domain.ILocalRepository
import com.itscryo.hermes.domain.IMessageDBRepository
import com.itscryo.hermes.global_model.LogTags
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Service to initialize the database and synchronize the local db with the firebase
 */
@AndroidEntryPoint
class InitializerService() : Service() {

	private val job = SupervisorJob()
	private val scope = CoroutineScope(Dispatchers.IO + job)

	@Inject
	lateinit var localRepo: ILocalRepository

	@Inject
	lateinit var firestoreRepo: IFirestoreRepository

	val tag = LogTags("Init Service")

	private var messageDBService: MessageDBService? = null


	private val connection = object : ServiceConnection {
		override fun onServiceConnected(className: ComponentName, service: IBinder) {
			val binder = service as MessageDBService.LocalBinder
			messageDBService = binder.getService()

		}

		override fun onServiceDisconnected(arg0: ComponentName) {
			messageDBService = null
		}

	}

	private suspend fun <T> withBind(lambda: suspend () -> T): T {
		bind()
		val result = lambda()
		unbind()
		return result
	}

	private fun bind() {
		Intent(this, MessageDBService::class.java).also { intent ->
			this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
		}
	}

	private fun unbind() {
		this.unbindService(connection)
	}

	inner class LocalBinder : Binder() {
		fun getService() = this@InitializerService
	}

	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		scope.launch {
			val userID = localRepo.retrieveUserCredAsync(applicationContext)
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


	private suspend fun setupUsers(userID: String) {
		val userInfo = firestoreRepo.getUserInfo(userID)
		if (userInfo == null) {
			Log.e(tag.error, "User info doesn't exist in the firestore")
			throw Throwable("User info doesn't exist in firestore")
		}
		val friendListString = userInfo.friendList
		withBind {
			messageDBService?.storeFirestoreUser(userInfo)

			for (elem in friendListString) {
				val tempUser = firestoreRepo.getUserInfo(elem) ?: continue

				messageDBService?.storeFirestoreUser(tempUser)

			}
		}

	}

	override fun onDestroy() {
		job.cancel()
		if (messageDBService != null) {
			unbind()
		}
		super.onDestroy()
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}
}
