package dev.drsn.karrots.audio

import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dev.drsn.karrots.audio.effects.EffectBassBoost
import dev.drsn.karrots.audio.effects.EffectReverb
import dev.drsn.karrots.audio.effects.EffectVirtualizer
import dev.drsn.karrots.audio.event.EventEmitter
import dev.drsn.karrots.audio.processor.AudioProcessor
import dev.drsn.karrots.audio.types.PlaybackState
import dev.drsn.karrots.extension.setMediaId
import dev.drsn.karrots.extension.toMediaItem
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.util.Random
import dev.drsn.karrots.util.Timer

class AudioPlayer(
	private val exoPlayer: ExoPlayer,
	val processor: AudioProcessor
) : ExoPlayer by exoPlayer, EventEmitter() {

	/** Player Internal Playlist Management **/
	private val queueTracksMap = hashMapOf<String, TrackPreview>()
	private var queueTracksKeys = arrayListOf<String>()
	private var unAlteredQueueTracksKeys = arrayListOf<String>()

	/** Audio Effects **/
	@UnstableApi
	val effectReverb = EffectReverb(audioSessionId)
	@UnstableApi
	val effectBassBoost = EffectBassBoost(audioSessionId)
	@UnstableApi
	val effectVirtualizer = EffectVirtualizer(audioSessionId)

	/** Speed, Pitch **/
	var playbackRate
		get() = playbackParameters.speed
		set(value) {
			playbackParameters = PlaybackParameters(value.coerceIn(0.1f..2f), playbackParameters.pitch)
		}

	var pitch
		get() = playbackParameters.pitch
		set(value) {
			playbackParameters = PlaybackParameters(playbackParameters.speed, value.coerceIn(0.1f..2f))
		}

	/** Extra ExoPlayer Features **/
	var isShuffling = false
		private set

	val queueTracks get() = queueTracksKeys.mapNotNull { queueTracksMap[it] }
	val queueCurrentIndex get() = exoPlayer.currentMediaItemIndex
	val queueCurrentItem
		get() = queueTracksKeys.getOrNull(exoPlayer.currentMediaItemIndex)?.let {
			if (queueTracksMap.containsKey(it)) return@let queueTracksMap.get(it)
			return@let null
		}

	var playbackState by mutableStateOf(
		if (exoPlayer.isPlaying) PlaybackState.Playing
		else if (exoPlayer.isLoading) PlaybackState.Buffering
		else PlaybackState.Paused
	)
		private set

	init {

		exoPlayer.playWhenReady = true
		var prevTime = exoPlayer.currentPosition
		var prevDuration = exoPlayer.duration

		/** Current Time Event Manager **/
		Timer().setInterval(250) {
			if (exoPlayer.currentPosition != prevTime) {
				prevTime = exoPlayer.currentPosition
				emitTimeUpdate()
			}
		}

		/** ExoPlayer Event Manager **/
		exoPlayer.addListener(object : Player.Listener {

			override fun onIsPlayingChanged(p: Boolean) {
				playbackState = if (p) PlaybackState.Playing
				else if (exoPlayer.isLoading) PlaybackState.Buffering
				else PlaybackState.Paused
				emitPlaybackStateChange(playbackState)
			}

			override fun onPlaybackStateChanged(state: Int) {
				when (state) {
					Player.STATE_READY -> emitCanPlay()
					Player.STATE_BUFFERING -> PlaybackState.Buffering.also {
						playbackState = it
						emitPlaybackStateChange(it)
					}

					else -> Unit
				}
			}

			override fun onPlayerErrorChanged(error: PlaybackException?) {
				if (error != null) emitError(error)
			}

			override fun onTracksChanged(tracks: Tracks) {
				if (prevDuration != exoPlayer.duration && exoPlayer.duration > 0L) {
					prevDuration = exoPlayer.duration.coerceAtLeast(1)
					emitDurationChange()
				}
			}

			override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = emitMediaItemChange()
		})
	}

	override fun play() {
		if (exoPlayer.mediaItemCount == 0 || exoPlayer.isPlaying) return
		if (exoPlayer.currentMediaItem != null && exoPlayer.currentPosition != exoPlayer.duration) return exoPlayer.play()
		exoPlayer.seekTo(0, 0)
		exoPlayer.prepare()
	}

	override fun pause() {
		if (exoPlayer.isPlaying && exoPlayer.isCurrentMediaItemSeekable) return exoPlayer.pause()
	}

	fun togglePlayPause() {
		if (exoPlayer.isPlaying) pause() else play()
	}

	override fun seekTo(positionMs: Long) {
		val playMaybe = exoPlayer.isPlaying
		exoPlayer.seekTo(positionMs)
		if (playMaybe) play() else pause()
	}

	fun jumpTo(index: Int) {
		if (exoPlayer.mediaItemCount == 0) return
		val position = if (index < 0) exoPlayer.mediaItemCount - index else index
		exoPlayer.seekTo(
			position.coerceIn(0 until exoPlayer.mediaItemCount),
			C.TIME_UNSET
		)
		exoPlayer.prepare()
		exoPlayer.play()
	}

	private fun shuffle() {
		isShuffling = true
		val currentItem = exoPlayer.currentMediaItem
		val currentIndex = exoPlayer.currentMediaItemIndex
		queueTracksKeys =
			(unAlteredQueueTracksKeys.shuffled(kotlin.random.Random(System.currentTimeMillis())) as ArrayList<String>)
				.apply {
					currentItem?.mediaId?.let {
						if (this.contains(it)) {
							this.remove(it)
							this.add(0, it)
						}
					}
				}
		removeMediaItemExcept(currentIndex)
		exoPlayer.addMediaItems(
			queueTracksKeys.mapNotNull { e -> if (e == currentItem?.mediaId) null else queueTracksMap[e]?.toMediaItem() }
		)
		emitShuffleChange()
		emitQueueChange()
	}

	private fun unShuffle() {
		isShuffling = false
		val currentItem = exoPlayer.currentMediaItem
		queueTracksKeys = unAlteredQueueTracksKeys
		val itsIndex = queueTracksKeys.indexOf(currentItem?.mediaId)
		removeMediaItemExcept(exoPlayer.currentMediaItemIndex)
		exoPlayer.addMediaItems(
			queueTracksKeys.mapNotNull { e -> if (e == currentItem?.mediaId) null else queueTracksMap[e]?.toMediaItem() }
		)
		exoPlayer.moveMediaItem(exoPlayer.currentMediaItemIndex, itsIndex)
		emitShuffleChange()
		emitQueueChange()
	}

	fun toggleShuffle() {
		if (isShuffling) unShuffle() else shuffle()
	}

	fun toggleRepeat() {
		exoPlayer.repeatMode = when (exoPlayer.repeatMode) {
			Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
			Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
			else -> Player.REPEAT_MODE_OFF
		}
		emitRepeatChange()
	}

	fun addTrack(track: TrackPreview) {
		var uuid = Random.generateCodeIncludingSymbols(12)
		while (queueTracksMap.containsKey(uuid)) uuid = Random.generateCodeIncludingSymbols(12)
		queueTracksKeys.add(uuid)
		unAlteredQueueTracksKeys.add(uuid)
		queueTracksMap[uuid] = track
		exoPlayer.addMediaItem(track.toMediaItem().setMediaId(uuid))
		emitQueueChange()
	}

	fun addTracks(tracks: List<TrackPreview>) {
		exoPlayer.addMediaItems(
			tracks.map { e ->
				var uuid = Random.generateCodeIncludingSymbols(12)
				while (queueTracksMap.containsKey(uuid)) uuid = Random.generateCodeIncludingSymbols(12)
				queueTracksKeys.add(uuid)
				unAlteredQueueTracksKeys.add(uuid)
				queueTracksMap[uuid] = e
				return@map e.toMediaItem().setMediaId(uuid)
			}
		)
		emitQueueChange()
	}

	fun setTrack(track: TrackPreview) {
		clearTracks()
		addTrack(track)
		jumpTo(0)
		emitQueueChange()
	}

	fun setTracks(tracks: List<TrackPreview>) {
		clearTracks()
		exoPlayer.setMediaItems(
			tracks.map { e ->
				var uuid = Random.generateCodeIncludingSymbols(12)
				while (queueTracksMap.containsKey(uuid)) uuid = Random.generateCodeIncludingSymbols(12)
				queueTracksKeys.add(uuid)
				unAlteredQueueTracksKeys.add(uuid)
				queueTracksMap[uuid] = e
				return@map e.toMediaItem().setMediaId(uuid)
			}
		)
		emitQueueChange()
	}

	private fun removeMediaItemExcept(index: Int) {
		if (exoPlayer.mediaItemCount == 0 || index < 0 || index >= exoPlayer.mediaItemCount) return
		when (index) {
			0 -> exoPlayer.removeMediaItems(1, exoPlayer.mediaItemCount)
			exoPlayer.mediaItemCount - 1 -> exoPlayer.removeMediaItems(0, exoPlayer.mediaItemCount - 1)
			else -> {
				val noOfStartItems =
					(exoPlayer.mediaItemCount - 1) - ((exoPlayer.mediaItemCount - 1) - index)
				exoPlayer.removeMediaItems(0, noOfStartItems)
				exoPlayer.removeMediaItems(1, exoPlayer.mediaItemCount)
			}
		}
	}

	fun clearTracks() {
		queueTracksMap.clear()
		queueTracksKeys.clear()
		exoPlayer.clearMediaItems()
		unAlteredQueueTracksKeys.clear()
		emitQueueChange()
	}

	@OptIn(UnstableApi::class)
	fun destroy() {
		if (exoPlayer.isPlaying) exoPlayer.stop()
		clearTracks()
		exoPlayer.release()
		processor.stereoProcessor.flush()
		processor.roboticProcessor.flush()
	}
}