package dev.drsn.karrots.ui.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun BottomNavigationBar(
	modifier: Modifier = Modifier,
	content: @Composable (RowScope.() -> Unit)
) {

	Layer(
		modifier = modifier
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 12.dp, bottom = 16.dp)
				.padding(horizontal = 12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceEvenly,
			content = content,
		)

	}
}

@Composable
fun BottomNavigationBarItem(
	selected: Boolean,
	onClick: () -> Unit,
	imageVector: ImageVector,
	label: String,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {

	val theme = LocalTheme.current
	val indication = LocalIndication.current

	Column(
		modifier = modifier
			.clickable(
				enabled = enabled,
				onClick = onClick,
				interactionSource = interactionSource,
				indication = null
			),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {

		Box(
			modifier = Modifier
				.width(64.dp)
				.height(32.dp)
				.clip(MaterialTheme.shapes.large)
				.background(if (selected) theme.colorScheme.secondaryContainer else Color.Transparent)
				.clickable(
					enabled = enabled,
					onClick = onClick,
					interactionSource = interactionSource,
					indication = indication
				),
			contentAlignment = Alignment.Center
		) {

			Icon(
				modifier = Modifier,
				imageVector = imageVector,
				contentDescription = "Icon",
				tint = if (selected) theme.colorScheme.onSecondaryContainer else theme.colorScheme.onSurfaceVariant
			)
		}

		Spacer(
			modifier = Modifier
				.height(4.dp)
		)

		Text(
			text = label,
			style = theme.typography.labelSmall,
			color = if (selected) theme.colorScheme.onSurface else theme.colorScheme.onSurfaceVariant
		)

	}

}
