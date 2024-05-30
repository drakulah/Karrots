package dev.drsn.karrots.audio.processor

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi

data class AudioProcessor @OptIn(UnstableApi::class) constructor(
	val stereoProcessor: StereoProcessor,
	val roboticProcessor: RoboticProcessor,
)
