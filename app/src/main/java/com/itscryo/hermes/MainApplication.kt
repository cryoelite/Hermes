package com.itscryo.hermes

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
	@SuppressLint("ObsoleteSdkInt")
	private fun createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = getString(R.string.default_fcm_channel_name)
			val descriptionText = getString(R.string.default_fcm_channel_desc)
			val channelID= getString(R.string.default_fcm_channel_id)
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel = NotificationChannel(channelID, name, importance).apply {
				description = descriptionText
			}
			// Register the channel with the system
			val notificationManager: NotificationManager =
				getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channel)
		}
	}

	private fun createNotificationBuilder(){
		// Create an explicit intent for an Activity in your app
		val intent = Intent(this, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
		val channelID= getString(R.string.default_fcm_channel_id)

		val builder = NotificationCompat.Builder(this, channelID)
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setContentTitle("Hermes")
			.setContentText("New Message")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			// Set the intent that will fire when the user taps the notification
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
	}
	override fun onCreate() {
		super.onCreate()
//		createNotificationChannel()
//		createNotificationBuilder()
	}

}