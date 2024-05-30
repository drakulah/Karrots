package dev.drsn.karrots.audio.session

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper.MediaStyle
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import dev.drsn.karrots.MainActivity
import dev.drsn.karrots.R
import dev.drsn.karrots.audio.AudioPlayer
import dev.drsn.karrots.audio.event.EventListener
import dev.drsn.karrots.extension.setSizeParam
import dev.drsn.karrots.innertube.utils.autoJoinToString

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlayerMediaSession(
	private val ctx: Context,
	private val player: AudioPlayer,
	private val channelId: String
) {

	private var albumArtRequest: Disposable? = null
	private val session = MediaSession.Builder(ctx, player).build()
	private val playerEventListener = object : EventListener {
		override fun onMediaItemChange() = loadThumbnail()
	}

	companion object {
		var albumArt by mutableStateOf<Bitmap?>(null)
		var albumArtUrl by mutableStateOf("default")
	}

	init {
		loadDefaultThumbnail()
		player.addEventListener(playerEventListener)
	}

	private fun loadDefaultThumbnail() {
		albumArtUrl = "default"
		albumArt = BitmapFactory.decodeResource(
			ctx.resources,
			R.mipmap.ic_launcher_foreground
		)
	}

	private fun loadThumbnail() {
		val currentSongThumbnail =
			player.queueCurrentItem?.thumbnails?.firstOrNull()?.url ?: return loadDefaultThumbnail()

		if (albumArtUrl == currentSongThumbnail) return loadDefaultThumbnail()

		albumArtRequest?.dispose()
		albumArtRequest = ctx.imageLoader.enqueue(
			ImageRequest.Builder(ctx)
				.data(currentSongThumbnail.setSizeParam(512))
				.allowHardware(false)
				.listener(
					onSuccess = { _, result ->
						albumArtUrl = currentSongThumbnail
						albumArt = (result.drawable as BitmapDrawable).bitmap
						// changeNotificationThumbnail()
					},
					onCancel = { loadDefaultThumbnail() },
					onError = { _, _ -> loadDefaultThumbnail() }
				)
				.build()
		)
	}

	fun mediaNotification(): Notification? {
		val currentMediaItem = player.queueCurrentItem ?: return null
		val mediaStyle = MediaStyle(session)
		return NotificationCompat.Builder(ctx, channelId)
			.setContentTitle(currentMediaItem.title)
			.setContentText(currentMediaItem.uploaders.autoJoinToString { e -> e.title })
			.setSmallIcon(R.mipmap.ic_launcher_foreground)
			.setLargeIcon(Icon.createWithResource(ctx, R.mipmap.ic_launcher_foreground))
			.setOnlyAlertOnce(true)
			.setCategory(NotificationCompat.CATEGORY_TRANSPORT)
			.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
			.setStyle(mediaStyle)
			.setContentIntent(
				PendingIntent.getActivity(
					ctx, 0,
					Intent(ctx, MainActivity::class.java),
					PendingIntent.FLAG_IMMUTABLE
				)
			)
			.build()
	}


	fun release() {
		player.removeEventListener(playerEventListener)
		session.release()
	}
}