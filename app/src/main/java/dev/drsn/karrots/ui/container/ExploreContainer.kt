package dev.drsn.karrots.ui.container

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.drsn.karrots.LocalContentLoader
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.ArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.ui.common.CardRow
import dev.drsn.karrots.ui.common.TopicText
import dev.drsn.karrots.ui.common.TwoColPager
import dev.drsn.karrots.ui.theme.LocalTheme

private val twoColRendererArr = arrayListOf(
	"moods & genres"
)

@Composable
fun ExploreContainer(
	controller: NavHostController,
) {

	val contentLoader = LocalContentLoader.current
	val player = LocalPlayer.current
	val lazyListState = rememberLazyListState()

	LazyColumn(
		modifier = Modifier
			.fillMaxSize(),
		state = lazyListState
	) {

		item {
			TopBar()
		}

		item {
			Spacer(
				modifier = Modifier
					.height(10.dp)
			)
		}

		items(contentLoader.explore.contents) {
			TopicText(
				text = it.topic.title,
				subTitle = it.topic.subtitle,
				thumbnails = it.topic.thumbnail
			)

			if (twoColRendererArr.contains(it.topic.title.lowercase()))
				TwoColPager(
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
			text = "Explore",
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