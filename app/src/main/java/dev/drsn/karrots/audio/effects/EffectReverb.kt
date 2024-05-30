package dev.drsn.karrots.audio.effects

import android.media.audiofx.PresetReverb

fun Short.toPresetString() = when (this) {
	PresetReverb.PRESET_SMALLROOM -> "Small Room"
	PresetReverb.PRESET_MEDIUMROOM -> "Medium Room"
	PresetReverb.PRESET_LARGEROOM -> "Large Room"
	PresetReverb.PRESET_MEDIUMHALL -> "Medium Hall"
	PresetReverb.PRESET_LARGEHALL -> "Large Hall"
	PresetReverb.PRESET_PLATE -> "Plate"
	else -> "None"
}

class EffectReverb(
	audioSessionId: Int
) {

	companion object {
		const val MIN_VALUE: Short = PresetReverb.PRESET_NONE
		const val MAX_VALUE: Short = PresetReverb.PRESET_PLATE
	}

	private val effect = PresetReverb(0, audioSessionId)

	var enabled
		get() = effect.enabled
		set(value) {
			effect.enabled = value
		}

	var value
		get() = effect.preset
		set(value) {
			effect.preset = value.coerceIn(MIN_VALUE, MAX_VALUE)
		}
}