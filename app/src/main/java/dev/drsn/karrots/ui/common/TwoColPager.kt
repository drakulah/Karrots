package dev.drsn.karrots.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.ArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.MoodPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.ui.theme.LocalTheme
import kotlin.math.ceil

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwoColPager(
	list: List<PreviewParser.ContentPreview>,
	onClick: (PreviewParser.ContentPreview) -> Unit = {}
) {

	val pagerState = rememberPagerState { ceil(list.size.toFloat() / 4f).toInt() }

	HorizontalPager(
		modifier = Modifier
			.fillMaxWidth(),
		state = pagerState,
		contentPadding = PaddingValues(horizontal = 13.dp),
		pageSize = PageSize.Fixed(350.dp),
		verticalAlignment = Alignment.Top
	) {

		val dataList = remember {
			val start = it * 4
			val end = (start + 4).coerceAtMost(list.size)
			val items = mutableStateListOf<PreviewParser.ContentPreview>()
			items.addAll(list.slice(start until end))
			items
		}

		EachCol(
			modifier = Modifier
				.fillMaxSize(),
			dataList = dataList,
			onClick = onClick
		)

	}
}

@Composable
private fun EachCol(
	modifier: Modifier = Modifier,
	dataList: List<PreviewParser.ContentPreview>,
	onClick: (PreviewParser.ContentPreview) -> Unit
) {

	Column(
		modifier = modifier
	) {

		for (pre in dataList) {
			if (pre is MoodPreview)
				MoodRow(data = pre)
			else
				TrackRow(
					data = pre,
					onClick = onClick
				)
		}
	}
}

@Composable
private fun MoodRow(
	data: MoodPreview
) {

	val theme = LocalTheme.current

	Row(
		modifier = Modifier
			.padding(vertical = 4.dp)
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.background(theme.colorScheme.surfaceBright)
			.clickable { }
			.padding(start = 8.dp)
			.padding(vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {

		Box(
			modifier = Modifier
				.width(5.dp)
				.height(40.dp)
				.background(
					color = Color(data.color),
					shape = RoundedCornerShape(2.dp)
				)
		)

		Text(
			text = data.title,
			style = theme.typography.titleMedium,
			color = theme.colorScheme.onPrimaryContainer,
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
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.clickable { onClick(data) }
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