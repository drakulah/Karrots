package dev.drsn.karrots.audio.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.upstream.DefaultAllocator
import dev.drsn.karrots.App
import dev.drsn.karrots.audio.AudioPlayer
import dev.drsn.karrots.audio.event.EventListener
import dev.drsn.karrots.audio.processor.AudioProcessor
import dev.drsn.karrots.audio.processor.RoboticProcessor
import dev.drsn.karrots.audio.processor.StereoProcessor
import dev.drsn.karrots.audio.session.PlayerMediaSession
import dev.drsn.karrots.audio.types.PlaybackState
import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.routes.player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlayerService : Service() {

	private lateinit var player: AudioPlayer
	private lateinit var innertube: Innertube
	private lateinit var audioProcessors: AudioProcessor
	private lateinit var mediaSession: PlayerMediaSession
	private lateinit var notificationManager: NotificationManager

	private var isForegroundStarted = false
	private val playerServiceBinder = PlayerServiceBinder()
	private val playerEventListener = object : EventListener {
		override fun onDurationChange() = updateNotification()
		override fun onPlaybackStateChange(state: PlaybackState) = updateNotification()
	}

	inner class PlayerServiceBinder : Binder() {
		fun getPlayer(): AudioPlayer = player
		fun getInnertube(): Innertube = innertube
	}

	override fun onBind(intent: Intent?): Binder {
		return playerServiceBinder
	}

	override fun onCreate() {
		super.onCreate()
		notificationManager = getSystemService(NotificationManager::class.java)
		innertube = Innertube()
		audioProcessors = AudioProcessor(
			stereoProcessor = StereoProcessor(),
			roboticProcessor = RoboticProcessor()
		)
		val renderFactory = object
			: DefaultRenderersFactory(this) {
			override fun buildAudioSink(
				context: Context,
				enableFloatOutput: Boolean,
				enableAudioTrackPlaybackParams: Boolean
			): AudioSink {
				return DefaultAudioSink.Builder(context)
					.setAudioProcessorChain(
						DefaultAudioSink.DefaultAudioProcessorChain(
							audioProcessors.stereoProcessor
						)
					)
					.build()
			}
		}
		player = AudioPlayer(
			ExoPlayer.Builder(baseContext)
				.setRenderersFactory(renderFactory)
				.setHandleAudioBecomingNoisy(true)
				.setMediaSourceFactory(DefaultMediaSourceFactory(generateDataSourceFactory()))
				.setLoadControl(
					DefaultLoadControl.Builder()
						.setAllocator(DefaultAllocator(true, 7 * 1024 * 1024))
						.build()
				)
				.build(),
			audioProcessors
		)

		mediaSession =
			PlayerMediaSession(baseContext, player, App.PLAYER_NOTIFICATION_CHANNEL_ID)

		player.addEventListener(playerEventListener)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		return START_STICKY
	}

	override fun onDestroy() {
		player.destroy()
		mediaSession.release()
		notificationManager.cancelAll()
		super.onDestroy()
		stopSelf()
	}

	private fun updateNotification() {
		val notification = mediaSession.mediaNotification()

		if (!isForegroundStarted) {
			if (notification != null) {
				startForeground(App.PLAYER_NOTIFICATION_ID, notification)
				isForegroundStarted = true
			}
		} else {
			if (notification != null) notificationManager.notify(App.PLAYER_NOTIFICATION_ID, notification)
		}
	}

	private fun generateDataSourceFactory() = ResolvingDataSource.Factory(
		DefaultDataSource.Factory(this)
	) { dataSpec ->

		val videoId = dataSpec.key ?: error("Video id not found")
		val playerDetails =
			runBlocking(Dispatchers.IO) { innertube.player(videoId) }
				?: error("Couldn't get track details")

		if (playerDetails.status != "OK") error("Item unplayable")

		val lastStream = playerDetails.streams.lastOrNull() ?: error("Invalid stream data")

		dataSpec.buildUpon()
			.setUri(lastStream.url)
			.build()
	}
}