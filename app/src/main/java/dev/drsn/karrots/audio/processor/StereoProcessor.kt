package dev.drsn.karrots.audio.processor

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

@UnstableApi
class StereoProcessor : BaseAudioProcessor() {

	companion object {
		const val MIN_VOLUME_LEVEL = 0f
		const val MAX_VOLUME_LEVEL = 1f
		const val DEFAULT_VOLUME_LEVEL = 1f
	}

	private var enabled = true
	private var leftVolLevel = DEFAULT_VOLUME_LEVEL
	private var rightVolLevel = DEFAULT_VOLUME_LEVEL

	val leftVolume: Float get() = leftVolLevel
	val rightVolume: Float get() = rightVolLevel

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
			/** LEFT  */
			inputSamples[i] = (inputSamples[i++] * leftVolLevel).toInt().toShort()
			/** RIGHT */
			inputSamples[i] = (inputSamples[i++] * rightVolLevel).toInt().toShort()
		}

		inputSamples.forEach { outputByteBuffer.putShort(it) }
		inputBuffer.position(position + remaining)
		outputBuffer.flip()
	}

	fun setLeftVolume(left: Float) {
		leftVolLevel = left
	}

	fun setRightVolume(right: Float) {
		rightVolLevel = right
	}

	fun setVolume(level: Float) {
		leftVolLevel = level
		rightVolLevel = level
	}

	fun setVolume(left: Float, right: Float) {
		leftVolLevel = left
		rightVolLevel = right
	}

	fun setEnabled(state: Boolean) {
		enabled = state
	}
}