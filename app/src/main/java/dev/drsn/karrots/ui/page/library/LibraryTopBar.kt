package dev.drsn.karrots.ui.page.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun LibraryTopBar() {

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
			text = "Library",
			style = theme.typography.headlineLarge,
			fontWeight = FontWeight.Bold,
			color = theme.colorScheme.primary
		)

		IconButton(
			onClick = { /*TODO*/ }
		) {
			Icon(
				imageVector = Icons.Sharp.Add,
				contentDescription = "Plus"
			)
		}
	}
}