package dev.drsn.karrots.ui.component

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.theme.LocalTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SeekBar(
	modifier: Modifier = Modifier,
	value: Long,
	min: Long = 0L,
	max: Long = 100L,
	onDragStart: (Long) -> Unit = {},
	onInput: (Long) -> Unit = {},
	onDragEnd: () -> Unit = {},
	progressColor: Color = LocalTheme.current.colorScheme.primary,
	backgroundColor: Color = LocalTheme.current.colorScheme.secondaryContainer,
	thumbColor: Color = progressColor,
	trackHeight: Dp = 2.8.dp,
	thumbHeight: Dp = trackHeight + 14.dp,
	animate: Boolean = true
) {

	val isDragging = remember { MutableTransitionState(false) }

	val amplitudeAnim by animateFloatAsState(
		targetValue = if (isDragging.targetState || !animate) 0f else 14f,
		label = "Amplitude"
	)
	val frequency by remember { mutableFloatStateOf(0.075f) }
	var phase by remember { mutableFloatStateOf(0f) }

	Box(
		modifier = modifier
			.pointerInput(min, max) {
				if (max < min) return@pointerInput
				var acc = 0f

				detectHorizontalDragGestures(
					onHorizontalDrag = { _, delta ->
						acc += delta / size.width * (max - min)

						if (acc !in -1f..1f) {
							onInput(acc.toLong())
							acc -= acc.toLong()
						}
					},
					onDragEnd = {
						acc = 0f
						isDragging.targetState = false
						onDragEnd()
					},
					onDragCancel = {
						acc = 0f
						isDragging.targetState = false
						onDragEnd()
					}
				)
			}
			.pointerInput(min, max) {
				if (max < min) return@pointerInput
				detectTapGestures(
					onPress = {
						isDragging.targetState = true
						onDragStart((it.x / size.width * (max - min) + min).toLong())
						awaitRelease()
						isDragging.targetState = false
						onDragEnd()
					},
					onTap = {
						isDragging.targetState = false
						onDragEnd()
					}
				)
			}
			.drawWithContent {
				val verticalCenter = size.height / 2f
				val progressXOffset =
					size.width * (value.toFloat() / max.toFloat()).coerceIn(0f..size.width)
				val lineHalfHeight = thumbHeight.toPx() / 2
				var lastStartOffset = Offset(x = progressXOffset, y = verticalCenter)

				for (i in progressXOffset.toLong() downTo 0L) {
					val y = amplitudeAnim * sin(sin(cos(frequency * i + phase)))
					val newEndOffset = Offset(i.toFloat(), y + size.height / 2)

					drawLine(
						color = progressColor,
						start = lastStartOffset,
						end = newEndOffset,
						strokeWidth = trackHeight.toPx(),
						cap = StrokeCap.Round
					)

					lastStartOffset = newEndOffset
				}

				drawLine(
					color = backgroundColor,
					start = Offset(x = progressXOffset + 1f, y = verticalCenter),
					end = Offset(x = size.width, y = verticalCenter),
					strokeWidth = trackHeight.toPx(),
					cap = StrokeCap.Round
				)

				drawLine(
					color = thumbColor,
					start = Offset(progressXOffset, verticalCenter - lineHalfHeight),
					end = Offset(progressXOffset, verticalCenter + lineHalfHeight),
					strokeWidth = trackHeight.toPx() + 0.8.dp.toPx(),
					cap = StrokeCap.Round
				)

				phase += 0.04f
			}
			.fillMaxWidth()
			.height(20.dp)
	)

}