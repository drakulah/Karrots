package dev.drsn.karrots

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache

class App : Application(), ImageLoaderFactory {

	companion object {
		const val PLAYER_NOTIFICATION_ID = 1
		const val PLAYER_NOTIFICATION_CHANNEL_ID = "karrots_player"
	}

	override fun onCreate() {
		createNotificationChannel()
		super.onCreate()
	}

	override fun newImageLoader() = ImageLoader.Builder(this)
		.crossfade(true)
		.respectCacheHeaders(false)
		.allowHardware(false)
		.diskCache(
			DiskCache.Builder()
				.directory(cacheDir.resolve("coil"))
				.maxSizeBytes(512 * 1024 * 1024L)
				.build()
		)
		.build()

	private fun createNotificationChannel() {
		val manager = getSystemService(NotificationManager::class.java)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			if (manager.getNotificationChannel(PLAYER_NOTIFICATION_CHANNEL_ID) == null) {
				manager.createNotificationChannel(
					NotificationChannel(
						PLAYER_NOTIFICATION_CHANNEL_ID,
						applicationInfo.name,
						NotificationManager.IMPORTANCE_DEFAULT
					).apply {
						setSound(null, null)
						enableLights(false)
						enableVibration(false)
					}
				)
			}
		}
	}

}