package dev.drsn.karrots.audio.event

import androidx.media3.common.PlaybackException
import dev.drsn.karrots.audio.types.PlaybackState

interface EventListener {

	fun onCanPlay() = Unit
	fun onMaybeSleep() = Unit
	fun onTimeUpdate() = Unit
	fun onQueueChange() = Unit
	fun onRepeatChange() = Unit
	fun onShuffleChange() = Unit
	fun onDurationChange() = Unit
	fun onMediaItemChange() = Unit
	fun onPlaybackStateChange(state: PlaybackState) = Unit
	fun onError(exception: PlaybackException) = Unit

}