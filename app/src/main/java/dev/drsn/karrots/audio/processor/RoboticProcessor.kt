package dev.drsn.karrots.audio.processor

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

@UnstableApi
class RoboticProcessor : BaseAudioProcessor() {

	companion object {
		const val MIN_PHASE_INC_LEVEL = 0f
		const val MAX_PHASE_INC_LEVEL = 0.05f
		const val DEFAULT_PHASE_INC_LEVEL = MIN_PHASE_INC_LEVEL
	}

	private var enabled = true
	private var phase = 0f
	private var phaseIncrement = DEFAULT_PHASE_INC_LEVEL

	override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
		return AudioProcessor.AudioFormat(inputAudioFormat.sampleRate, 2, C.ENCODING_PCM_16BIT)
	}

	override fun isActive(): Boolean {
		return enabled
	}

	override fun queueInput(inputBuffer: ByteBuffer) {
		val position = inputBuffer.position()
		val remaining = inputBuffer.remaining()
		val outputSize = remaining * 2 // Doubling the output size for stereo

		val inputSamples = ShortArray(remaining / 2)
		val outputBuffer = replaceOutputBuffer(outputSize)
		val outputByteBuffer = outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

		inputBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(inputSamples)

		var i = 0
		while (i < inputSamples.size) {
			inputSamples[i] = (inputSamples[i++] * sin(phase.toDouble())).toInt().toShort()
			phase += phaseIncrement
		}

		inputSamples.forEach { outputByteBuffer.putShort(it) }
		inputBuffer.position(position + remaining)
		outputBuffer.flip()
	}

	fun setPhaseIncrement(inc: Float) {
		phaseIncrement = inc
	}

	fun setEnabled(state: Boolean) {
		enabled = state
	}
}