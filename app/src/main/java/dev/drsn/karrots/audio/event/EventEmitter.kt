package dev.drsn.karrots.audio.event

import androidx.media3.common.PlaybackException
import dev.drsn.karrots.audio.types.PlaybackState

open class EventEmitter {

	private val eventListeners = arrayListOf<EventListener>()

	fun addEventListener(listener: EventListener) {
		eventListeners.add(listener)
	}

	fun removeEventListener(listener: EventListener) {
		eventListeners.remove(listener)
	}

	protected fun emitCanPlay() {
		eventListeners.forEach { it.onCanPlay() }
	}

	protected fun emitMaybeSleep() {
		eventListeners.forEach { it.onMaybeSleep() }
	}

	protected fun emitTimeUpdate() {
		eventListeners.forEach { it.onTimeUpdate() }
	}

	protected fun emitQueueChange() {
		eventListeners.forEach { it.onQueueChange() }
	}

	protected fun emitRepeatChange() {
		eventListeners.forEach { it.onRepeatChange() }
	}

	protected fun emitShuffleChange() {
		eventListeners.forEach { it.onShuffleChange() }
	}

	protected fun emitDurationChange() {
		eventListeners.forEach { it.onDurationChange() }
	}

	protected fun emitMediaItemChange() {
		eventListeners.forEach { it.onMediaItemChange() }
	}

	protected fun emitPlaybackStateChange(state: PlaybackState) {
		eventListeners.forEach { it.onPlaybackStateChange(state) }
	}

	protected fun emitError(exception: PlaybackException) {
		eventListeners.forEach { it.onError(exception) }
	}

}