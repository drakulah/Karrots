package dev.drsn.karrots.ui.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.media3.common.util.UnstableApi
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.audio.effects.EffectBassBoost
import dev.drsn.karrots.audio.effects.EffectReverb
import dev.drsn.karrots.audio.effects.EffectVirtualizer
import dev.drsn.karrots.audio.processor.StereoProcessor
import dev.drsn.karrots.ui.component.Slider
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun ModalEqualizer() {

	LazyColumn(
		modifier = Modifier
			.padding(bottom = 12.dp)
			.padding(horizontal = 12.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		item { VolumeEq() }

		item { SpeedEq() }

		item { PitchEq() }

		item { BassBoostEq() }

		item { VirtualizerEq() }

		item { ReverbEq() }

	}
}

@OptIn(UnstableApi::class)
@Composable
fun SpeedEq() {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val max = (2f * 100).toLong()
	val min = (0.1f * 100).toLong()

	var playbackRate by remember { mutableLongStateOf((player.playbackRate * 100).toLong()) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				color = theme.colorScheme.surfaceContainer,
				shape = RoundedCornerShape(24.dp)
			)
			.padding(horizontal = 24.dp)
			.padding(top = 12.dp, bottom = 24.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {

			Text(
				text = "Speed (${String.format("%.2f", (playbackRate / 100f))}x)",
				style = theme.typography.bodyLarge,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.onSecondaryContainer
			)

			FilledTonalButton(
				onClick = {
					playbackRate = 100L
					player.playbackRate = 1f
				}
			) {

				Text(
					text = "Reset",
					style = theme.typography.bodyMedium,
					fontWeight = FontWeight.Normal,
					color = theme.colorScheme.onSecondaryContainer
				)
			}
		}

		Slider(
			modifier = Modifier
				.fillMaxWidth(),
			value = playbackRate,
			min = min,
			max = max,
			onDragStart = {
				playbackRate = it
				player.playbackRate = playbackRate / 100f
			},
			onInput = {
				playbackRate = (playbackRate + it).coerceIn(min..max)
				player.playbackRate = playbackRate / 100f
			}
		)

	}
}

@OptIn(UnstableApi::class)
@Composable
fun PitchEq() {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val max = (2f * 100).toLong()
	val min = (0.1f * 100).toLong()

	var pitch by remember { mutableLongStateOf((player.pitch * 100).toLong()) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				color = theme.colorScheme.surfaceContainer,
				shape = RoundedCornerShape(24.dp)
			)
			.padding(horizontal = 24.dp)
			.padding(top = 12.dp, bottom = 24.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {

			Text(
				text = "Pitch (${String.format("%.2f", (pitch / 100f))}x)",
				style = theme.typography.bodyLarge,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.onSecondaryContainer
			)

			FilledTonalButton(
				onClick = {
					pitch = 100L
					player.pitch = 1f
				}
			) {

				Text(
					text = "Reset",
					style = theme.typography.bodyMedium,
					fontWeight = FontWeight.Normal,
					color = theme.colorScheme.onSecondaryContainer
				)
			}
		}

		Slider(
			modifier = Modifier
				.fillMaxWidth(),
			value = pitch,
			min = min,
			max = max,
			onDragStart = {
				pitch = it
				player.pitch = pitch / 100f
			},
			onInput = {
				pitch = (pitch + it).coerceIn(min..max)
				player.pitch = pitch / 100f
			}
		)

	}
}

@OptIn(UnstableApi::class)
@Composable
fun VolumeEq() {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val max = (StereoProcessor.MAX_VOLUME_LEVEL * 100L).toLong()
	val min = (StereoProcessor.MIN_VOLUME_LEVEL * 100L).toLong()

	var leftVolume by remember { mutableLongStateOf((player.processor.stereoProcessor.leftVolume * 100L).toLong()) }
	var rightVolume by remember { mutableLongStateOf((player.processor.stereoProcessor.rightVolume * 100L).toLong()) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				color = theme.colorScheme.surfaceContainer,
				shape = RoundedCornerShape(24.dp)
			)
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {

		Text(
			text = "Volume (L - ${lerp(min, max, leftVolume / 100f)}% | R - ${
				lerp(
					min,
					max,
					rightVolume / 100f
				)
			}%)",
			style = theme.typography.bodyLarge,
			fontWeight = FontWeight.Normal,
			color = theme.colorScheme.onSecondaryContainer
		)

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {

			Slider(
				modifier = Modifier
					.weight(0.5f),
				value = leftVolume,
				min = min,
				max = max,
				onDragStart = {
					leftVolume = it
				},
				onInput = {
					leftVolume = (leftVolume + it).coerceIn(min..max)
					player.processor.stereoProcessor.setLeftVolume(leftVolume / 100f)
				}
			)

			Slider(
				modifier = Modifier
					.weight(0.5f),
				value = rightVolume,
				min = min,
				max = max,
				onDragStart = {
					rightVolume = it
				},
				onInput = {
					rightVolume = (rightVolume + it).coerceIn(min..max)
					player.processor.stereoProcessor.setRightVolume(rightVolume / 100f)
				}
			)
		}

	}
}

@OptIn(UnstableApi::class)
@Composable
fun BassBoostEq() {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val min = EffectBassBoost.MIN_VALUE.toLong()
	val max = EffectBassBoost.MAX_VALUE.toLong()

	var enabled by remember { mutableStateOf(player.effectBassBoost.enabled) }
	var value by remember { mutableLongStateOf(player.effectBassBoost.value.toLong()) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				color = theme.colorScheme.surfaceContainer,
				shape = RoundedCornerShape(24.dp)
			)
			.padding(horizontal = 24.dp)
			.padding(top = 12.dp, bottom = 24.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {

			Text(
				text = "Bass (${String.format("%.2f", (value.toFloat() / max.toFloat()) * 100f)}%)",
				style = theme.typography.bodyLarge,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.onSecondaryContainer
			)

			FilledTonalButton(
				onClick = {
					enabled = !enabled
					player.effectBassBoost.enabled = enabled
				}
			) {

				Text(
					text = if (enabled) "Disable" else "Enable",
					style = theme.typography.bodyMedium,
					fontWeight = FontWeight.Normal,
					color = theme.colorScheme.onSecondaryContainer
				)
			}
		}

		Slider(
			modifier = Modifier
				.fillMaxWidth(),
			enabled = enabled,
			value = value,
			min = min,
			max = max,
			onDragStart = {
				if (!enabled) return@Slider
				value = it
				player.effectBassBoost.value = value.toShort()
			},
			onInput = {
				if (!enabled) return@Slider
				value = (value + it).coerceIn(min..max)
				player.effectBassBoost.value = value.toShort()
			}
		)

	}
}

@OptIn(UnstableApi::class)
@Composable
fun VirtualizerEq() {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val min = EffectVirtualizer.MIN_VALUE.toLong()
	val max = EffectVirtualizer.MAX_VALUE.toLong()

	var enabled by remember { mutableStateOf(player.effectVirtualizer.enabled) }
	var value by remember { mutableLongStateOf(player.effectVirtualizer.value.toLong()) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				color = theme.colorScheme.surfaceContainer,
				shape = RoundedCornerShape(24.dp)
			)
			.padding(horizontal = 24.dp)
			.padding(top = 12.dp, bottom = 24.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {

			Text(
				text = "Virtualizer (${String.format("%.2f", (value.toFloat() / max.toFloat()) * 100f)}%)",
				style = theme.typography.bodyLarge,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.onSecondaryContainer
			)

			FilledTonalButton(
				onClick = {
					enabled = !enabled
					player.effectVirtualizer.enabled = enabled
				}
			) {

				Text(
					text = if (enabled) "Disable" else "Enable",
					style = theme.typography.bodyMedium,
					fontWeight = FontWeight.Normal,
					color = theme.colorScheme.onSecondaryContainer
				)
			}
		}

		Slider(
			modifier = Modifier
				.fillMaxWidth(),
			enabled = enabled,
			value = value,
			min = min,
			max = max,
			onDragStart = {
				if (!enabled) return@Slider
				value = it
				player.effectVirtualizer.value = value.toShort()
			},
			onInput = {
				if (!enabled) return@Slider
				value = (value + it).coerceIn(min..max)
				player.effectVirtualizer.value = value.toShort()
			}
		)

	}
}

@OptIn(UnstableApi::class)
@Composable
fun ReverbEq() {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val min = EffectReverb.MIN_VALUE.toLong()
	val max = EffectReverb.MAX_VALUE.toLong()

	var enabled by remember { mutableStateOf(player.effectReverb.enabled) }
	var value by remember { mutableLongStateOf(player.effectReverb.value.toLong()) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				color = theme.colorScheme.surfaceContainer,
				shape = RoundedCornerShape(24.dp)
			)
			.padding(horizontal = 24.dp)
			.padding(top = 12.dp, bottom = 24.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {

			Text(
				text = "Reverb (${String.format("%.2f", (value.toFloat() / max.toFloat()) * 100f)}%)",
				style = theme.typography.bodyLarge,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.onSecondaryContainer
			)

			FilledTonalButton(
				onClick = {
					enabled = !enabled
					player.effectReverb.enabled = enabled
				}
			) {

				Text(
					text = if (enabled) "Disable" else "Enable",
					style = theme.typography.bodyMedium,
					fontWeight = FontWeight.Normal,
					color = theme.colorScheme.onSecondaryContainer
				)
			}
		}

		Slider(
			modifier = Modifier
				.fillMaxWidth(),
			enabled = enabled,
			value = value,
			min = min,
			max = max,
			onDragStart = {
				if (!enabled) return@Slider
				value = it
				player.effectReverb.value = value.toShort()
			},
			onInput = {
				if (!enabled) return@Slider
				value = (value + it).coerceIn(min..max)
				player.effectReverb.value = value.toShort()
			}
		)

	}
}