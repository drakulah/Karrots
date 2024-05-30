package dev.drsn.karrots.ui.container

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.sharp.Download
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material.icons.sharp.Shuffle
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.extension.generateDesc
import dev.drsn.karrots.extension.setSizeParam
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.ArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.innertube.utils.joinToString
import dev.drsn.karrots.loader.rememberPlaylistLoaderState
import dev.drsn.karrots.ui.common.CardRow
import dev.drsn.karrots.ui.common.TopicText
import dev.drsn.karrots.ui.component.Layer
import dev.drsn.karrots.ui.theme.LocalTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PlaylistContainer(
	browseId: String,
	controller: NavHostController
) {

	val theme = LocalTheme.current
	val player = LocalPlayer.current
	val config = LocalConfiguration.current

	val listState = rememberLazyListState()
	val playlistState = rememberPlaylistLoaderState(browseId)

	val visibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
	val visibleItemOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset.toFloat() } }

	var shallShuffle by remember { mutableStateOf(true) }
	var infoBoxHeight by remember { mutableFloatStateOf(0f) }
	var bgGradientStartColor by remember { mutableStateOf(theme.colorScheme.surface) }

	val listOnlyMinHeight = (config.screenHeightDp - 40).dp
	val infoBoxScrollProgress =
		if (visibleItemIndex == 0) (visibleItemOffset / infoBoxHeight.coerceAtLeast(1f)).coerceIn(0f..1f)
		else if (visibleItemIndex > 0) 1f
		else 0f

	val albumArtSizeRatio = ((1f - infoBoxScrollProgress * 2f)).coerceIn(0.4f..1f)

	val bgGradientStartColorAnim by animateColorAsState(
		targetValue = bgGradientStartColor,
		label = "Gradient"
	)
	val albumArtOpacity by animateFloatAsState(
		targetValue = if ((1f - infoBoxScrollProgress * 2f) < 0.2f) 0f else 1f,
		label = "Opacity"
	)

	LaunchedEffect(listState) {
		snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
			.distinctUntilChanged()
			.collect { lastVisibleIndex ->
				val totalItemsCount = playlistState.tracks.size
				if (lastVisibleIndex != null) {
					if (lastVisibleIndex >= totalItemsCount - 5) {
						playlistState.loadNextContinuation()
					}
				}
			}
	}

	Layer(
		modifier = Modifier
			.fillMaxSize()
	) {

		LazyColumn(
			state = listState,
			modifier = Modifier
				.fillMaxSize(),
			contentPadding = PaddingValues(bottom = 200.dp)
		) {

			item {

				Column(
					modifier = Modifier
						.fillMaxWidth()
						.background(
							brush = Brush.verticalGradient(
								colors = listOf(
									bgGradientStartColorAnim,
									theme.colorScheme.surface
								)
							)
						)
						.onGloballyPositioned {
							infoBoxHeight = it.size.height.toFloat()
						},
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {

					Spacer(
						modifier = Modifier
							.statusBarsPadding()
							.height(16.dp)
					)

					Box(
						modifier = Modifier
							.fillMaxWidth()
							.height(240.dp)
							.alpha(albumArtOpacity),
						contentAlignment = Alignment.BottomCenter
					) {

						AsyncImage(
							modifier = Modifier
								.size(240.dp.times(albumArtSizeRatio))
								.background(theme.colorScheme.primaryContainer)
								.shadow(10.dp),
							model = playlistState.thumbnail.firstOrNull()?.url?.setSizeParam(360),
							contentDescription = "Album Art",
							onSuccess = { state ->
								val bitmap = (state.result.drawable as BitmapDrawable).bitmap
								Palette.Builder(bitmap)
									.generate()
									.dominantSwatch
									?.hsl
									?.also {
										bgGradientStartColor = Color.hsl(it[0], it[1], 0.3f)
									}
							}
						)
					}

					Text(
						modifier = Modifier
							.padding(horizontal = 16.dp),
						text = playlistState.title,
						style = theme.typography.headlineSmall,
						fontWeight = FontWeight.SemiBold,
						color = theme.colorScheme.onSurface
					)

					Text(
						modifier = Modifier
							.padding(horizontal = 16.dp),
						text = listOf(
							"Playlist",
							playlistState.uploaders.autoJoinToString { e -> e.title },
							playlistState.yearText,
						).joinToString(joiner = " • ") { e -> e ?: "" },
						style = theme.typography.labelLarge,
						fontWeight = FontWeight.Medium,
						color = theme.colorScheme.onSurface
					)

					Text(
						modifier = Modifier
							.padding(horizontal = 16.dp),
						text = listOf(
							playlistState.viewCount,
							playlistState.trackCount,
							playlistState.playlistDuration
						).joinToString(joiner = " • ") { e -> e ?: "" },
						style = theme.typography.labelMedium,
						fontWeight = FontWeight.Normal,
						color = theme.colorScheme.onSurfaceVariant
					)

					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(
								start = 4.dp,
								end = 16.dp
							),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(4.dp)
					) {

						IconButton(
							onClick = { /*TODO*/ }
						) {
							Icon(
								imageVector = Icons.Outlined.LibraryAdd,
								contentDescription = "Icon"
							)
						}

						IconButton(
							onClick = { /*TODO*/ }
						) {
							Icon(
								imageVector = Icons.Sharp.Download,
								contentDescription = "Icon"
							)
						}

						IconButton(
							onClick = { /*TODO*/ }
						) {
							Icon(
								imageVector = Icons.Sharp.MoreVert,
								contentDescription = "Icon"
							)
						}

						Spacer(
							modifier = Modifier
								.weight(1f)
						)

						IconToggleButton(
							checked = shallShuffle,
							onCheckedChange = {
								shallShuffle = it
							}
						) {
							Icon(
								imageVector = Icons.Sharp.Shuffle,
								contentDescription = "Icon"
							)
						}

						FilledIconButton(
							modifier = Modifier
								.size(50.dp),
							onClick = {
								player.clearTracks()
								(if (shallShuffle) playlistState.tracks.shuffled() else playlistState.tracks)
									.forEach(player::addTrack)
								player.jumpTo(0)
							}
						) {
							Icon(
								modifier = Modifier
									.size(30.dp),
								imageVector = Icons.Sharp.PlayArrow,
								contentDescription = "Icon"
							)
						}

					}

				}
			}

			item {
				Spacer(
					modifier = Modifier
						.height(16.dp)
				)
			}

			items(playlistState.tracks) {
				TrackRow(
					data = it,
					onClick = player::setTrack
				)
			}

			items(playlistState.others) {
				Spacer(
					modifier = Modifier
						.height(10.dp)
				)

				TopicText(
					text = it.topic.title,
					subTitle = it.topic.subtitle,
					thumbnails = it.topic.thumbnail
				)

				CardRow(
					list = it.preContents,
					onClick = { pre ->
						when (pre) {
							is PlaylistPreview -> controller.navigate(ContainerRoute.Playlist.browseId(pre.browseId))
							is AlbumPreview -> controller.navigate(ContainerRoute.Album.browseId(pre.browseId))
							is ArtistPreview -> controller.navigate(ContainerRoute.Artist.browseId(pre.browseId))
							is TrackPreview -> player.setTrack(pre)
							else -> {}
						}
					}
				)
			}

			item {
				Spacer(
					modifier = Modifier
						.height(
							(listOnlyMinHeight - 71.dp.times(playlistState.tracks.size) - 200.dp).coerceAtLeast(0.dp)
						)
				)
			}

		}

		TopBar(
			controller = controller,
			title = playlistState.title,
			titleOpacity = ((infoBoxScrollProgress - 0.5f).coerceIn(0f..0.5f) * 2.5f).coerceIn(0f..1f),
			backgroundOpacity = ((infoBoxScrollProgress - 0.4f).coerceIn(0f..0.6f) * 8f).coerceIn(0f..1f)
		)
	}
}

@Composable
private fun TopBar(
	title: String? = null,
	titleOpacity: Float = 0f,
	backgroundOpacity: Float = 0f,
	controller: NavHostController,
) {
	val theme = LocalTheme.current

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(85.dp)
			.background(
				theme.colorScheme.surface.copy(
					alpha = backgroundOpacity
				)
			)
			.padding(end = 16.dp)
			.statusBarsPadding(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {

		IconButton(
			onClick = controller::popBackStack
		) {
			Icon(
				modifier = Modifier,
				imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
				contentDescription = "Back"
			)
		}

		if (title != null) Text(
			text = title,
			style = theme.typography.titleLarge,
			fontWeight = FontWeight.Medium,
			color = theme.colorScheme.onSurface.copy(alpha = titleOpacity),
			maxLines = 1,
			overflow = TextOverflow.Ellipsis
		)
	}
}

@Composable
private fun TrackRow(
	data: TrackPreview,
	onClick: (TrackPreview) -> Unit
) {

	val theme = LocalTheme.current

	Row(
		modifier = Modifier
			.padding(horizontal = 8.dp)
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.clickable(
				onClick = { onClick(data) }
			)
			.padding(start = 8.dp)
			.padding(vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp)
	) {

		AsyncImage(
			modifier = Modifier
				.size(55.dp)
				.clip(RoundedCornerShape(4.dp)),
			model = data.thumbnails.firstOrNull()?.url,
			contentDescription = "Album Art"
		)

		Column(
			modifier = Modifier
				.weight(1f)
		) {

			Text(
				text = data.title,
				style = theme.typography.titleMedium,
				color = theme.colorScheme.onSurface,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)

			Text(
				text = data.generateDesc(),
				style = theme.typography.bodyMedium,
				color = theme.colorScheme.secondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)

		}

		IconButton(
			modifier = Modifier,
			onClick = {}
		) {
			Icon(
				imageVector = Icons.Sharp.MoreVert,
				contentDescription = "MoreVertical"
			)
		}

	}
}