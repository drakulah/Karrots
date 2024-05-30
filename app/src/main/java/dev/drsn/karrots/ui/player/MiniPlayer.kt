package dev.drsn.karrots.ui.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Pause
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material.icons.sharp.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.audio.event.EventListener
import dev.drsn.karrots.audio.types.PlaybackState
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.ui.theme.LocalTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MiniPlayer(
	modifier: Modifier = Modifier,
) {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	var currentItem by remember { mutableStateOf(player.queueCurrentItem) }
	var playbackState by remember { mutableStateOf(player.playbackState) }
	var currentTime by remember { mutableLongStateOf(player.currentPosition) }
	var duration by remember { mutableLongStateOf(player.duration.coerceAtLeast(1)) }
	var thumbnail by remember { mutableStateOf(currentItem?.thumbnails?.lastOrNull()?.url ?: "") }

	DisposableEffect(Unit) {
		val eventListener = object : EventListener {
			override fun onPlaybackStateChange(state: PlaybackState) {
				playbackState = state
			}

			override fun onDurationChange() {
				duration = player.duration.coerceAtLeast(1)
			}

			override fun onTimeUpdate() {
				currentTime = player.currentPosition
			}

			override fun onMediaItemChange() {
				currentItem = player.queueCurrentItem
				duration = player.duration.coerceAtLeast(1)
				thumbnail = currentItem?.thumbnails?.lastOrNull()?.url ?: ""
			}
		}
		player.addEventListener(eventListener)
		onDispose { player.removeEventListener(eventListener) }
	}

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(60.dp)
			.then(modifier)
			.padding(horizontal = 18.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {

		AsyncImage(
			modifier = Modifier
				.size(44.dp)
				.clip(RoundedCornerShape(2.dp)),
			contentScale = ContentScale.Crop,
			model = thumbnail,
			contentDescription = "Album Art"
		)

		Column(
			modifier = Modifier
				.weight(1f)
		) {

			Text(
				modifier = Modifier
					.basicMarquee(),
				text = currentItem?.title ?: "",
				style = theme.typography.bodyMedium,
				fontWeight = FontWeight.Medium,
				color = theme.colorScheme.onSurface
			)

			Text(
				text = currentItem?.uploaders?.autoJoinToString { e -> e.title } ?: "",
				style = theme.typography.bodyMedium,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.onSurfaceVariant,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		}

		Row {
			IconButton(
				modifier = Modifier
					.drawWithContent {
						val strokeWidth = 6.dp
						val sizeDecrement = 10.dp

						drawArc(
							color = theme.colorScheme.secondaryContainer,
							startAngle = -90f,
							sweepAngle = 360f,
							useCenter = true,
							size = Size(
								width = size.width - sizeDecrement.toPx(),
								height = size.height - sizeDecrement.toPx()
							),
							topLeft = Offset(
								x = sizeDecrement.toPx() / 2f,
								y = sizeDecrement.toPx() / 2f
							),
						)

						drawArc(
							color = theme.colorScheme.primary,
							startAngle = -90f,
							sweepAngle = (currentTime.toFloat() / duration.toFloat()) * 360f,
							useCenter = true,
							size = Size(
								width = size.width - sizeDecrement.toPx(),
								height = size.height - sizeDecrement.toPx()
							),
							topLeft = Offset(
								x = sizeDecrement.toPx() / 2f,
								y = sizeDecrement.toPx() / 2f
							),
						)

						drawArc(
							color = theme.colorScheme.surfaceContainerHigh,
							startAngle = -90f,
							sweepAngle = 360f,
							useCenter = true,
							size = Size(
								width = size.width - sizeDecrement.toPx() - strokeWidth.toPx(),
								height = size.height - sizeDecrement.toPx() - strokeWidth.toPx()
							),
							topLeft = Offset(
								x = (strokeWidth.toPx() + sizeDecrement.toPx()) / 2f,
								y = (strokeWidth.toPx() + sizeDecrement.toPx()) / 2f
							),
						)

						drawContent()
					},
				onClick = player::togglePlayPause
			) {
				Icon(
					imageVector = if (playbackState == PlaybackState.Playing) Icons.Sharp.Pause
					else Icons.Sharp.PlayArrow,
					contentDescription = "Pause"
				)
			}

			IconButton(
				onClick = player::seekToNext
			) {
				Icon(
					imageVector = Icons.Sharp.SkipNext,
					contentDescription = "Next"
				)
			}
		}
	}

}