package dev.drsn.karrots.ui.container

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.sharp.Mic
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.ArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.loader.rememberSearchLoaderState
import dev.drsn.karrots.ui.common.TopicText
import dev.drsn.karrots.ui.component.Chip
import dev.drsn.karrots.ui.component.Chips
import dev.drsn.karrots.ui.component.SearchField
import dev.drsn.karrots.ui.theme.LocalTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SearchContainer(
	controller: NavHostController,
	query: String? = null
) {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	var userQuery by remember { mutableStateOf(query ?: "") }
	var isSearchFieldActive by remember { mutableStateOf(true) }

	val searchState = rememberSearchLoaderState(query = userQuery)
	val listState = rememberLazyListState()

	val chips = remember { searchState.chips }
	val contents = remember { searchState.previewContents }
	val continuationContents = remember { searchState.continuationContents }
	val topSearchResult = searchState.previewTopResults

	LaunchedEffect(listState) {
		snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
			.distinctUntilChanged()
			.collect { lastVisibleIndex ->
				val totalItemsCount = continuationContents.size
				if (lastVisibleIndex != null) {
					if (lastVisibleIndex >= totalItemsCount - 10) {
						searchState.loadNextContinuation()
					}
				}
			}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.statusBarsPadding()
	) {

		Column(
			modifier = Modifier
				.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.statusBarsPadding()
//				.padding(end = 16.dp)
					.padding(top = 3.dp),
				verticalAlignment = Alignment.CenterVertically
			) {

				IconButton(
					onClick = { }
				) {

					Icon(
						imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
						contentDescription = "Back"
					)
				}

				SearchField(
					modifier = Modifier
						.weight(1f),
					query = userQuery,
					isActive = isSearchFieldActive,
					onQueryChange = { userQuery = it },
					onActiveChange = { isSearchFieldActive = it },
					onSearch = {
						if (userQuery.isNotEmpty()) controller.navigate(ContainerRoute.Search.query(userQuery))
					},
					placeholder = "Search songs & videos"
				)

				IconButton(
					onClick = { }
				) {

					Icon(
						imageVector = Icons.Sharp.Mic,
						contentDescription = "Back"
					)
				}
			}

			Chips(
				modifier = Modifier
					.fillMaxWidth()
			) {

				chips.forEachIndexed { i, s ->
					Chip(
						text = s.title,
						active = if (searchState.activeChipIndex != null) (searchState.activeChipIndex == i) else s.isCurrent,
						onClick = { searchState.loadFromChipIndex(if (searchState.activeChipIndex == i) null else i) }
					)
				}
			}

			HorizontalDivider(
				modifier = Modifier
					.fillMaxWidth(),
				thickness = 2.dp,
				color = theme.colorScheme.surfaceContainer
			)
		}

		if (searchState.activeChipIndex == null) {
			LazyColumn {

				item {
					if (topSearchResult == null) return@item

					Spacer(
						modifier = Modifier
							.height(10.dp)
					)

					TopicText(
						text = topSearchResult.title
					)

					Column(
						modifier = Modifier
							.fillMaxWidth()
					) {

						TrackRow(
							data = topSearchResult.topResult,
							onClick = { pre ->
								when (pre) {
									is PlaylistPreview -> controller.navigate(ContainerRoute.Playlist.browseId(pre.browseId))
									is AlbumPreview -> controller.navigate(ContainerRoute.Album.browseId(pre.browseId))
									is ArtistPreview -> controller.navigate(ContainerRoute.Artist.browseId(pre.browseId))
									is TrackPreview -> {
										player.addTrack(pre)
										player.jumpTo(player.mediaItemCount)
									}

									else -> {}
								}
							}
						)

						if (topSearchResult.otherPriorResults.isNotEmpty()) HorizontalDivider(
							modifier = Modifier
								.padding(vertical = 1.dp, horizontal = 16.dp)
								.fillMaxWidth(),
							thickness = 2.dp,
							color = theme.colorScheme.surfaceContainer
						)

						topSearchResult.otherPriorResults.forEach {
							TrackRow(
								data = it,
								onClick = { pre ->
									when (pre) {
										is PlaylistPreview -> controller.navigate(ContainerRoute.Playlist.browseId(pre.browseId))
										is AlbumPreview -> controller.navigate(ContainerRoute.Album.browseId(pre.browseId))
										is ArtistPreview -> controller.navigate(ContainerRoute.Artist.browseId(pre.browseId))
										is TrackPreview -> {
											player.addTrack(pre)
											player.jumpTo(player.mediaItemCount)
										}

										else -> {}
									}
								}
							)
						}

					}
				}

				items(contents) { container ->

					Spacer(
						modifier = Modifier
							.height(10.dp)
					)

					TopicText(
						text = container.topic.title,
						subTitle = container.topic.subtitle,
						thumbnails = container.topic.thumbnail
					)

					container.preContents.forEach { data ->
						TrackRow(
							data = data,
							onClick = { pre ->
								when (pre) {
									is PlaylistPreview -> controller.navigate(ContainerRoute.Playlist.browseId(pre.browseId))
									is AlbumPreview -> controller.navigate(ContainerRoute.Album.browseId(pre.browseId))
									is ArtistPreview -> controller.navigate(ContainerRoute.Artist.browseId(pre.browseId))
									is TrackPreview -> {
										player.addTrack(pre)
										player.jumpTo(player.mediaItemCount)
									}

									else -> {}
								}
							}
						)
					}
				}

				item {
					Spacer(
						modifier = Modifier
							.height(200.dp)
					)
				}

			}
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize(),
				state = listState
			) {

				item {
					Spacer(
						modifier = Modifier
							.height(10.dp)
					)
				}

				items(continuationContents) { data ->
					TrackRow(
						data = data,
						onClick = { pre ->
							when (pre) {
								is PlaylistPreview -> controller.navigate(ContainerRoute.Playlist.browseId(pre.browseId))
								is AlbumPreview -> controller.navigate(ContainerRoute.Album.browseId(pre.browseId))
								is ArtistPreview -> controller.navigate(ContainerRoute.Artist.browseId(pre.browseId))
								is TrackPreview -> {
									player.addTrack(pre)
									player.jumpTo(player.mediaItemCount)
								}

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
		}

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