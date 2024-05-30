package dev.drsn.karrots.audio.effects

import android.media.audiofx.BassBoost

class EffectBassBoost(
	audioSessionId: Int
) {

	companion object {
		const val MIN_VALUE: Short = 0
		const val MAX_VALUE: Short = 1000
	}

	private val effect = BassBoost(0, audioSessionId)

	var enabled
		get() = effect.enabled
		set(value) {
			effect.enabled = value
		}

	var value
		get() = effect.roundedStrength
		set(value) {
			effect.setStrength(value.coerceIn(MIN_VALUE, MAX_VALUE))
		}
}