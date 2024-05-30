package dev.drsn.karrots.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun Chips(
	modifier: Modifier = Modifier,
	content: @Composable (RowScope.() -> Unit)
) {

	Layer(
		modifier = Modifier
			.fillMaxWidth()
	) {

		LazyRow(
			modifier = modifier
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {

			item {
				Spacer(
					modifier = Modifier
						.width(12.dp)
				)
			}

			item {
				Row(
					modifier = modifier
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					content = content
				)
			}

			item {
				Spacer(
					modifier = Modifier
						.width(12.dp)
				)
			}

		}
	}

}

@Composable
fun Chip(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	text: String,
	active: Boolean = false,
) {

	val theme = LocalTheme.current

	val borderRadiusModifier = if (active) Modifier
	else Modifier
		.border(BorderStroke(1.dp, theme.colorScheme.outlineVariant), RoundedCornerShape(8.dp))

	Row(
		modifier = modifier
			.clip(RoundedCornerShape(8.dp))
			.then(borderRadiusModifier)
			.background(if (active) theme.colorScheme.surfaceTint else theme.colorScheme.surfaceContainerLow)
			.clickable(
				enabled = true,
				onClick = onClick
			)
			.padding(horizontal = 18.dp, vertical = 10.dp)
	) {

		Text(
			text = text,
			style = theme.typography.bodyMedium,
			color = if (active) theme.colorScheme.onPrimary else theme.colorScheme.onSurface
		)

	}


}