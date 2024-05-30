package dev.drsn.karrots.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.drsn.karrots.innertube.parser.partial.chunk.ThumbnailInfo
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun TopicText(
	modifier: Modifier = Modifier,
	text: String,
	subTitle: String? = null,
	thumbnails: List<ThumbnailInfo> = arrayListOf(),
	onClick: (() -> Unit)? = null
) {

	val theme = LocalTheme.current

	Row(
		modifier = modifier
			.fillMaxWidth()
			.clickable(
				enabled = onClick != null,
				indication = null,
				interactionSource = remember { MutableInteractionSource() },
				onClick = onClick ?: {}
			)
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {

		if (thumbnails.isNotEmpty()) AsyncImage(
			modifier = Modifier
				.size(50.dp)
				.clip(RoundedCornerShape(6.dp)),
			model = thumbnails.firstOrNull()?.url,
			contentDescription = "Album Art"
		)

		Column(
			modifier = modifier
				.weight(1f),
			horizontalAlignment = Alignment.Start
		) {

			if (!subTitle.isNullOrEmpty()) Text(
				modifier = Modifier
					.fillMaxWidth(),
				text = subTitle,
				style = theme.typography.labelLarge,
				fontWeight = FontWeight.Normal,
				color = theme.colorScheme.onSurfaceVariant,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)

			Text(
				modifier = Modifier
					.fillMaxWidth(),
				text = text,
				style = theme.typography.headlineMedium,
				fontWeight = FontWeight.Bold,
				color = theme.colorScheme.onSurface,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)

		}
	}
}