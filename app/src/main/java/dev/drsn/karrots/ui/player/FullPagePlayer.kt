package dev.drsn.karrots.ui.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Lyrics
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material.icons.sharp.Pause
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material.icons.sharp.Repeat
import androidx.compose.material.icons.sharp.RepeatOne
import androidx.compose.material.icons.sharp.Shuffle
import androidx.compose.material.icons.sharp.SkipNext
import androidx.compose.material.icons.sharp.SkipPrevious
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dev.drsn.karrots.LocalBottomModalSheet
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.audio.event.EventListener
import dev.drsn.karrots.audio.types.PlaybackState
import dev.drsn.karrots.extension.toTimeString
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.ui.component.BottomSheetState
import dev.drsn.karrots.ui.component.Carousel
import dev.drsn.karrots.ui.component.CarouselDefaults
import dev.drsn.karrots.ui.component.SeekBar
import dev.drsn.karrots.ui.theme.LocalTheme
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullPagePlayer(
	modifier: Modifier = Modifier,
	bottomSheetState: BottomSheetState
) {

	val theme = LocalTheme.current
	val player = LocalPlayer.current
	val config = LocalConfiguration.current
	val modalSheet = LocalBottomModalSheet.current
	val coroutineScope = rememberCoroutineScope()

	val scrWidth = config.screenWidthDp.dp
	val scrHeight = config.screenHeightDp.dp
	val remainingSpaceH = scrWidth - (230.dp)
	val verticalSpacing = (0.035f * scrHeight.value).dp
	val thumbnailHeight = (0.36f * scrHeight.value).dp


	var currentItem by remember { mutableStateOf(player.queueCurrentItem) }
	var playbackState by remember { mutableStateOf(player.playbackState) }
	var currentTime by remember { mutableLongStateOf(player.currentPosition) }
	var duration by remember { mutableLongStateOf(player.duration.coerceAtLeast(1)) }
	var seekProgress by remember { mutableStateOf<Long?>(null) }
	var repeatMode by remember { mutableIntStateOf(player.repeatMode) }
	var isShuffling by remember { mutableStateOf(player.isShuffling) }

	val thumbnails = remember { mutableStateListOf<String>() }
	val carouselState = rememberPagerState(player.queueCurrentIndex) { thumbnails.size }

//	LaunchedEffect(Unit) {
//		modalSheet.setContent {
//			ModalEqualizer()
//		}.expandSoft()
//	}

	LaunchedEffect(Unit) {
		thumbnails.addAll(
			player.queueTracks.map { e ->
				e.thumbnails.lastOrNull()?.url ?: ""
			}
		)
	}

	LaunchedEffect(carouselState.settledPage) {
		if (player.queueCurrentIndex != carouselState.settledPage) player.jumpTo(carouselState.settledPage)
	}

	DisposableEffect(Unit) {
		val eventListener = object : EventListener {
			override fun onRepeatChange() {
				repeatMode = player.repeatMode
			}

			override fun onShuffleChange() {
				isShuffling = player.isShuffling
			}

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
				coroutineScope.launch {
					carouselState.animateScrollToPage(player.queueCurrentIndex)
				}
			}

			override fun onQueueChange() {
				thumbnails.clear()
				thumbnails.addAll(player.queueTracks.map { e -> e.thumbnails.lastOrNull()?.url ?: "" })
			}
		}
		player.addEventListener(eventListener)
		onDispose { player.removeEventListener(eventListener) }
	}

	Column(
		modifier = modifier
			.fillMaxWidth()
			.height(scrHeight)
			.requiredHeight(scrHeight)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.statusBarsPadding()
				.padding(horizontal = 12.dp, vertical = 5.dp)
		) {

			IconButton(
				onClick = bottomSheetState::collapseSoft
			) {
				Icon(
					imageVector = Icons.Sharp.KeyboardArrowDown,
					contentDescription = "ChevronDown"
				)
			}

			Spacer(
				modifier = Modifier
					.weight(1f)
			)

			IconButton(
				onClick = { /*TODO*/ }
			) {
				Icon(
					imageVector = Icons.Outlined.Lyrics,
					contentDescription = "Lyrics"
				)
			}

			IconButton(
				onClick = { /*TODO*/ }
			) {
				Icon(
					imageVector = Icons.AutoMirrored.Outlined.QueueMusic,
					contentDescription = "Queue"
				)
			}

			IconButton(
				onClick = {
					modalSheet.setContent {
						ModalEqualizer()
					}.expandSoft()
				}
			) {
				Icon(
					imageVector = Icons.Outlined.Equalizer,
					contentDescription = "Equalizer"
				)
			}

			IconButton(
				onClick = {
					modalSheet.setContent {
						Box(
							modifier = Modifier
								.padding(horizontal = 7.dp)
								.padding(bottom = 7.dp)
								.systemBarsPadding()
								.fillMaxWidth()
								.height(500.dp)
								.background(
									color = theme.colorScheme.secondaryContainer,
									shape = RoundedCornerShape(24.dp)
								)
						)
					}.expandSoft()
				}
			) {
				Icon(
					imageVector = Icons.Sharp.MoreVert,
					contentDescription = "MoreVertical"
				)
			}

		}

		Spacer(
			modifier = Modifier
				.height(verticalSpacing)
		)

//		ImageCarousel(
//			sliderList = thumbnails,
//			pagerState = pagerState,
//			imageHeight = thumbnailHeight,
//			contentPadding = PaddingValues(horizontal = remainingSpaceH / 2),
//		)

		Carousel(
			models = thumbnails,
			pagerState = carouselState,
			contentPadding = PaddingValues(horizontal = remainingSpaceH / 2),
			imageStyle = CarouselDefaults.defaultImageStyle(
				height = thumbnailHeight
			)
		)


//		HorizontalPager(
//			modifier = Modifier
//				.fillMaxWidth()
//				.height(activeHeight),
//			state = pagerState,
//			contentPadding = PaddingValues(horizontal = remainingSpaceH / 2),
//			pageSpacing = 14.dp
//		) {
//			AsyncImage(
//				modifier = Modifier
//					.width(if (it == pagerState.settledPage) activeHeight else deActiveHeight)
//					.height(if (it == pagerState.settledPage) activeHeight else deActiveHeight)
//					.clip(RoundedCornerShape(12.dp))
//					.background(theme.colorScheme.secondaryContainer),
//				model = thumbnails[it].setSizeParam(512),
//				contentScale = ContentScale.Crop,
//				contentDescription = "Album Art"
//			)
//		}

		Spacer(
			modifier = Modifier
				.height(verticalSpacing)
		)

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 36.dp)
		) {

			Text(
				modifier = Modifier
					.basicMarquee(),
				text = currentItem?.title ?: "",
				style = theme.typography.headlineMedium,
				fontWeight = FontWeight.Medium,
				color = theme.colorScheme.primary
			)

			Text(
				text = currentItem?.uploaders?.autoJoinToString { e -> e.title } ?: "",
				style = theme.typography.bodyLarge,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.secondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		}

		Spacer(
			modifier = Modifier
				.height(verticalSpacing)
		)

		SeekBar(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 36.dp),
			value = (seekProgress ?: currentTime).coerceIn(0..duration),
			min = 0,
			max = duration,
			onDragStart = {
				seekProgress = it
			},
			onInput = {
				if (seekProgress != null) seekProgress = (it + seekProgress!!).coerceIn(0L..duration)
			},
			onDragEnd = {
				if (seekProgress != null) {
					player.seekTo(seekProgress!!)
					currentTime = seekProgress!!
				}
				seekProgress = null
			},
			animate = playbackState == PlaybackState.Playing
		)

		Row(
			modifier = Modifier
				.padding(top = 10.dp)
				.fillMaxWidth()
				.padding(horizontal = 36.dp)
		) {
			Text(
				text = (seekProgress ?: currentTime).toTimeString(),
				style = theme.typography.bodyMedium,
				fontWeight = FontWeight.Medium,
				fontSize = 13.sp,
				color = theme.colorScheme.secondary,
			)

			Spacer(
				modifier = Modifier
					.weight(1f)
			)

			Text(
				text = duration.toTimeString(),
				style = theme.typography.bodyMedium,
				fontWeight = FontWeight.Medium,
				fontSize = 13.sp,
				color = theme.colorScheme.secondary
			)
		}

		Spacer(
			modifier = Modifier
				.height(verticalSpacing * 0.6f)
		)

		Box(
			modifier = Modifier
				.fillMaxWidth(),
			contentAlignment = Alignment.Center
		) {

			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(15.dp)
			) {

				IconToggleButton(
					checked = isShuffling,
					onCheckedChange = { player.toggleShuffle() }
				) {
					Icon(
						modifier = Modifier
							.size(22.dp),
						imageVector = Icons.Sharp.Shuffle,
						contentDescription = "Shuffle"
					)
				}

				IconButton(
					onClick = player::seekToPrevious
				) {
					Icon(
						modifier = Modifier
							.size(30.dp),
						imageVector = Icons.Sharp.SkipPrevious,
						contentDescription = "Previous"
					)
				}

				FilledTonalButton(
					modifier = Modifier
						.size(70.dp),
					shape = CircleShape,
					contentPadding = PaddingValues(),
					onClick = player::togglePlayPause
				) {
					Icon(
						modifier = Modifier
							.size(32.dp),
						imageVector = if (playbackState == PlaybackState.Playing) Icons.Sharp.Pause
						else Icons.Sharp.PlayArrow,
						contentDescription = "Pause"
					)
				}

				IconButton(
					onClick = player::seekToNext
				) {
					Icon(
						modifier = Modifier
							.size(30.dp),
						imageVector = Icons.Sharp.SkipNext,
						contentDescription = "Next"
					)
				}

				IconToggleButton(
					checked = repeatMode != ExoPlayer.REPEAT_MODE_OFF,
					onCheckedChange = { player.toggleRepeat() }
				) {
					Icon(
						modifier = Modifier
							.size(22.dp),
						imageVector = if (repeatMode == ExoPlayer.REPEAT_MODE_ONE) Icons.Sharp.RepeatOne
						else Icons.Sharp.Repeat,
						contentDescription = "Repeat"
					)
				}

			}
		}

	}
}