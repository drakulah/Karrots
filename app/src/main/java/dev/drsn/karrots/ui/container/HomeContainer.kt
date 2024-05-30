package dev.drsn.karrots.ui.container

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.ArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.loader.rememberHomeLoaderState
import dev.drsn.karrots.ui.common.CardRow
import dev.drsn.karrots.ui.common.TopicText
import dev.drsn.karrots.ui.common.TwoColPager
import dev.drsn.karrots.ui.component.Chip
import dev.drsn.karrots.ui.component.Chips
import dev.drsn.karrots.ui.component.Layer
import dev.drsn.karrots.ui.theme.LocalTheme

private val twoColRendererArr = arrayListOf(
	"quick picks",
	"long listening",
	"trending songs",
	"top music videos",
	"recommended music videos",
	"covers and remixes",
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContainer(
	controller: NavHostController,
) {
	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val lazyListState = rememberLazyListState()
	val homeState = rememberHomeLoaderState(true)

	val chips = remember { homeState.chips }
	val contents = remember { homeState.contents }

	LazyColumn(
		modifier = Modifier
			.fillMaxSize(),
		state = lazyListState
	) {

		item {
			TopBar()
		}

		stickyHeader {
			Column(
				modifier = Modifier
					.fillMaxWidth()
			) {

				Layer(
					modifier = Modifier
						.background(theme.colorScheme.surface)
						.padding(top = 30.dp, bottom = 10.dp)
				) {

					Chips(
						modifier = Modifier
							.fillMaxWidth()
					) {

						chips.forEachIndexed { i, s ->
							Chip(
								text = s.title,
								active = if (homeState.activeChipIndex != null) (homeState.activeChipIndex == i) else s.isCurrent,
								onClick = { homeState.loadFromChipIndex(i) }
							)
						}
					}
				}

				HorizontalDivider(
					modifier = Modifier
						.fillMaxWidth(),
					thickness = 1.5.dp,
					color = if (lazyListState.canScrollBackward) theme.colorScheme.surfaceContainer else Color.Transparent
				)
			}
		}

		item {
			Spacer(
				modifier = Modifier
					.height(10.dp)
			)
		}

		items(contents) { item ->

			TopicText(
				text = item.topic.title,
				subTitle = item.topic.subtitle,
				thumbnails = item.topic.thumbnail
			)

			if (twoColRendererArr.contains(item.topic.title.lowercase()))
				TwoColPager(
					list = item.preContents,
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
			else
				CardRow(
					list = item.preContents,
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

			Spacer(
				modifier = Modifier
					.height(20.dp)
			)
		}

		item {
			Spacer(
				modifier = Modifier
					.height(150.dp)
			)
		}

	}

}

@Composable
private fun TopBar() {

	val theme = LocalTheme.current
	val ctx = LocalContext.current

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 20.dp)
			.padding(top = 10.dp)
			.statusBarsPadding(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {

		Text(
			text = ctx.getString(ctx.applicationInfo.labelRes),
			style = theme.typography.headlineLarge,
			fontWeight = FontWeight.Bold,
			color = theme.colorScheme.onSurface
		)

		IconButton(
			onClick = { /*TODO*/ }
		) {

			Icon(
				imageVector = Icons.Outlined.Settings,
				contentDescription = "Settings"
			)
		}
	}
}