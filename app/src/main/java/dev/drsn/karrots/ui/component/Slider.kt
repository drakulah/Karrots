package dev.drsn.karrots.ui.component

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun Slider(
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	value: Long,
	min: Long = 0L,
	max: Long = 100L,
	onDragStart: (Long) -> Unit = {},
	onInput: (Long) -> Unit = {},
	onDragEnd: () -> Unit = {},
	thumbSize: Dp = 24.dp,
	inactiveTrackHeight: Dp = 4.dp
) {

	val theme = LocalTheme.current
	val isDragging = remember { MutableTransitionState(false) }
	val progress = (value.toFloat() / max.toFloat()).coerceIn(0f..1f)

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
				val thumbSizePx = thumbSize.toPx()
				val thumbSizeHalfPx = thumbSizePx / 2f
				val inactiveTrackHeightPx = inactiveTrackHeight.toPx()
				val paddingX = (size.width - thumbSizePx) * progress + thumbSizeHalfPx

				drawCircle(
					color = if (enabled) theme.colorScheme.primary else theme.colorScheme.onSurface,
					radius = thumbSizeHalfPx,
					center = Offset(x = thumbSizeHalfPx, y = center.y)
				)

				drawRoundRect(
					color = if (enabled) theme.colorScheme.secondaryContainer else theme.colorScheme.surfaceVariant,
					topLeft = Offset(x = paddingX, y = center.y - inactiveTrackHeightPx / 2f),
					size = Size(width = size.width - paddingX, height = inactiveTrackHeightPx),
					cornerRadius = CornerRadius(size.maxDimension)
				)

				drawLine(
					color = if (enabled) theme.colorScheme.primary else theme.colorScheme.onSurface,
					start = Offset(x = thumbSizeHalfPx, y = center.y),
					end = Offset(x = paddingX, y = center.y),
					strokeWidth = thumbSizePx
				)

				drawCircle(
					color = if (enabled) theme.colorScheme.primary else theme.colorScheme.onSurface,
					radius = thumbSizeHalfPx,
					center = Offset(x = paddingX, y = center.y)
				)

				drawCircle(
					color = theme.colorScheme.surface,
					radius = thumbSizeHalfPx - 3.dp.toPx(),
					center = Offset(x = paddingX, y = center.y)
				)
			}
			.fillMaxWidth()
			.height(thumbSize)
	)

}