package dev.drsn.karrots.ui.container

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.sharp.FavoriteBorder
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import dev.drsn.karrots.LocalInnertube
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.extension.setSizeParam
import dev.drsn.karrots.innertube.parser.Artist
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.ArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.routes.artist
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.ui.common.CardRow
import dev.drsn.karrots.ui.common.TopicText
import dev.drsn.karrots.ui.component.Layer
import dev.drsn.karrots.ui.theme.LocalTheme
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ArtistContainer(
	browseId: String,
	controller: NavHostController
) {

	val theme = LocalTheme.current
	val player = LocalPlayer.current
	val innertube = LocalInnertube.current

	val listState = rememberLazyListState()
	val coroutineContext = rememberCoroutineScope()

	val visibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
	val visibleItemOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset.toFloat() } }

	var infoBoxHeight by remember { mutableFloatStateOf(0f) }
	var artistContent by remember { mutableStateOf<Artist?>(null) }
	var bgGradientStartColor by remember { mutableStateOf(theme.colorScheme.surface) }

	val infoBoxScrollProgress =
		if (visibleItemIndex == 0) (visibleItemOffset / infoBoxHeight.coerceAtLeast(1f)).coerceIn(0f..1f)
		else if (visibleItemIndex > 0) 1f
		else 0f

	val bgGradientStartColorAnim by animateColorAsState(
		targetValue = bgGradientStartColor,
		label = "Gradient"
	)

	DisposableEffect(browseId) {
		val job = coroutineContext.launch(Dispatchers.IO) {
			if (artistContent != null) return@launch
			artistContent = innertube.artist(browseId)
		}

		onDispose { job.cancel() }
	}

	Layer(
		modifier = Modifier
			.fillMaxSize()
	) {

		if (artistContent != null) LazyColumn(
			state = listState,
			modifier = Modifier
				.fillMaxSize()
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

					Box(
						modifier = Modifier
							.fillMaxWidth(),
						contentAlignment = Alignment.BottomCenter
					) {

						AsyncImage(
							modifier = Modifier
								.fillMaxWidth()
								.height(300.dp)
								.background(theme.colorScheme.primaryContainer),
							model = artistContent!!.thumbnail.lastOrNull()?.url?.setSizeParam(512),
							contentDescription = "Album Art",
							contentScale = ContentScale.Crop,
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

						Box(
							modifier = Modifier
								.fillMaxWidth()
								.height(60.dp)
								.background(
									brush = Brush.verticalGradient(
										colors = listOf(
											Color.Transparent,
											theme.colorScheme.surface.copy(0.8f),
										)
									)
								)
								.padding(horizontal = 16.dp),
							contentAlignment = Alignment.BottomStart
						) {
							Text(
								text = artistContent!!.title,
								style = theme.typography.headlineSmall,
								fontWeight = FontWeight.SemiBold,
								color = theme.colorScheme.onSurface,
								maxLines = 2,
								overflow = TextOverflow.Ellipsis
							)
						}
					}

					Text(
						modifier = Modifier
							.padding(horizontal = 16.dp),
						text = "${artistContent!!.subscribers} subscribers",
						style = theme.typography.labelLarge,
						fontWeight = FontWeight.Medium,
						color = theme.colorScheme.onSurface
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
								imageVector = Icons.Sharp.FavoriteBorder,
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

						FilledIconButton(
							modifier = Modifier
								.size(50.dp),
							onClick = { }
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

			items(artistContent!!.others) {
				Spacer(
					modifier = Modifier
						.height(16.dp)
				)

				TopicText(
					text = it.topic.title,
					subTitle = it.topic.subtitle,
					thumbnails = it.topic.thumbnail
				)

				if (it.topic.title.lowercase() == "songs")
					it.preContents.forEach { eachPre ->
						TrackRow(
							data = eachPre,
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
				else
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
						.height(200.dp)
				)
			}

		}

		TopBar(
			controller = controller,
			title = artistContent?.title,
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
			.padding(
				start = 12.dp * (1f - backgroundOpacity),
				end = 16.dp
			)
			.statusBarsPadding(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {

		IconButton(
			onClick = controller::popBackStack,
			colors = IconButtonDefaults.filledIconButtonColors(
				containerColor = theme.colorScheme.surface
			)
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
	data: PreviewParser.ContentPreview,
	onClick: (PreviewParser.ContentPreview) -> Unit
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
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {

		AsyncImage(
			modifier = Modifier
				.size(55.dp)
				.clip(RoundedCornerShape(4.dp)),
			model = when (data) {
				is TrackPreview -> data.thumbnails.firstOrNull()?.url
				is AlbumPreview -> data.thumbnails.firstOrNull()?.url
				is ArtistPreview -> data.thumbnails.firstOrNull()?.url
				is PlaylistPreview -> data.thumbnails.firstOrNull()?.url
				else -> null
			},
			contentDescription = "Album Art"
		)

		Column(
			modifier = Modifier
				.weight(1f),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {

			Text(
				text = when (data) {
					is TrackPreview -> data.title
					is AlbumPreview -> data.title
					is ArtistPreview -> data.title
					is PlaylistPreview -> data.title
					else -> ""
				},
				style = theme.typography.titleMedium,
				color = theme.colorScheme.onSurface,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)

			Text(
				text = when (data) {
					is TrackPreview -> data.uploaders.autoJoinToString { e -> e.title }
					is AlbumPreview -> data.uploaders.autoJoinToString { e -> e.title }
					is ArtistPreview -> data.subscriberCount
					is PlaylistPreview -> data.trackCount
					else -> null
				} ?: "",
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