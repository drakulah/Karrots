package dev.drsn.karrots.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.drsn.karrots.extension.SizeMode
import dev.drsn.karrots.extension.stickHeight
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.ArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun CardRow(
	list: List<PreviewParser.ContentPreview>,
	onClick: (PreviewParser.ContentPreview) -> Unit = {}
) {

	val dataList = remember {
		val arr = mutableListOf<PreviewParser.ContentPreview>()
		arr.addAll(list)
		arr
	}

	LazyRow(
		modifier = Modifier
			.fillMaxWidth()
			.stickHeight(SizeMode.Highest),
		horizontalArrangement = Arrangement.spacedBy(6.dp)
	) {

		item {
			Spacer(
				modifier = Modifier
					.width(8.dp)
			)
		}

		items(dataList) {
			EachCard(
				data = it,
				onClick = onClick
			)
		}

		item {
			Spacer(
				modifier = Modifier
					.width(8.dp)
			)
		}

	}
}

@Composable
private fun EachCard(
	data: PreviewParser.ContentPreview,
	onClick: (PreviewParser.ContentPreview) -> Unit
) {

	val theme = LocalTheme.current

	val m by remember {
		mutableFloatStateOf(
			if (
				data is TrackPreview &&
				data.thumbnails.firstOrNull().let {
					it ?: return@let false
					it.width != it.height
				}
			) 1.78f else 1f
		)
	}

	Column(
		modifier = Modifier
			.width((160 * m + 12).dp)
			.clip(RoundedCornerShape(6.dp))
			.clickable { onClick(data) }
			.padding(6.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp)
	) {

		Box(
			modifier = Modifier,
			contentAlignment = Alignment.BottomEnd
		) {

			AsyncImage(
				modifier = Modifier
					.width((160 * m).dp)
					.height(160.dp)
					.clip(
						if (data is ArtistPreview) CircleShape
						else RoundedCornerShape(6.dp)
					),
				model = when (data) {
					is TrackPreview -> data.thumbnails.firstOrNull()?.url
					is AlbumPreview -> data.thumbnails.firstOrNull()?.url
					is ArtistPreview -> data.thumbnails.firstOrNull()?.url
					is PlaylistPreview -> data.thumbnails.firstOrNull()?.url
					else -> null
				},
				contentDescription = "Album Art"
			)

			if (data is TrackPreview) IconButton(
				modifier = Modifier
					.padding(8.dp),
				onClick = { onClick(data) },
				colors = IconButtonDefaults.iconButtonColors(
					containerColor = Color.Black.copy(0.6f),
					contentColor = Color.White
				)
			) {

				Icon(
					imageVector = Icons.Sharp.PlayArrow,
					contentDescription = "PlayArrow"
				)
			}
		}

		Column(
			modifier = Modifier,
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
				maxLines = 2,
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

	}
}