package dev.drsn.karrots.ui.page.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.component.Chip
import dev.drsn.karrots.ui.component.Chips
import dev.drsn.karrots.ui.component.Layer
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun LibraryPage() {

	val theme = LocalTheme.current
	var activeChipIndex by remember { mutableIntStateOf(0) }
	val lazyListState = rememberLazyListState()
	val chips = listOf("Playlists", "Podcasts", "Songs", "Albums", "Artists")

	LazyColumn(
		modifier = Modifier
			.fillMaxSize(),
		state = lazyListState
	) {

		item {
			LibraryTopBar()
		}

		item {

			Column(
				modifier = Modifier
					.fillMaxWidth()
			) {
				Layer(
					modifier = Modifier
						.background(theme.colorScheme.background)
						.padding(top = 30.dp, bottom = 10.dp)
				) {
					Chips(
						modifier = Modifier
							.fillMaxWidth()
					) {

						chips.forEachIndexed { i, s ->

							Chip(
								text = s,
								active = i == activeChipIndex,
								onClick = {
									activeChipIndex = i
								}
							)
						}
					}
				}
			}
		}

		item {
			Row(
				modifier = Modifier
					.padding(top = 3.dp)
					.fillMaxWidth()
					.padding(horizontal = 22.dp),
				verticalAlignment = Alignment.CenterVertically
			) {

				Row(
					modifier = Modifier,
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(2.dp)
				) {
					Text(
						text = "Name",
						style = theme.typography.labelLarge,
						fontWeight = FontWeight.Normal,
						color = theme.colorScheme.secondary
					)

					Icon(
						modifier = Modifier
							.size(20.dp),
						imageVector = Icons.Sharp.KeyboardArrowDown,
						contentDescription = "ChevronDown",
						tint = theme.colorScheme.secondary
					)
				}

				Spacer(modifier = Modifier.weight(1f))

				IconButton(
					onClick = { /*TODO*/ }
				) {
					Icon(
						modifier = Modifier
							.size(20.dp),
						imageVector = Icons.Outlined.GridView,
						contentDescription = "Grid",
						tint = theme.colorScheme.secondary
					)
				}

			}
		}

//		items(items = arrayListOf()) {
//
//			Row(
//				modifier = Modifier
//					.padding(horizontal = 12.dp, vertical = 2.dp)
//					.fillMaxWidth()
//					.clip(RoundedCornerShape(8.dp))
//					.clickable { }
//					.padding(start = 8.dp)
//					.padding(vertical = 8.dp),
//				verticalAlignment = Alignment.CenterVertically,
//				horizontalArrangement = Arrangement.spacedBy(12.dp)
//			) {
//
//
//				AsyncImage(
//					modifier = Modifier
//						.size(55.dp)
//						.clip(RoundedCornerShape(4.dp)),
//					model = it.displayImage,
//					contentDescription = "Album Art"
//				)
//
//				Column(
//					modifier = Modifier
//						.weight(1f),
//					verticalArrangement = Arrangement.spacedBy(4.dp)
//				) {
//
//					Text(
//						text = it.title,
//						style = theme.typography.titleMedium,
//						color = theme.colorScheme.primary,
//						maxLines = 1,
//						overflow = TextOverflow.Ellipsis
//					)
//
//					Text(
//						text = it.subtitle,
//						style = theme.typography.bodyMedium,
//						color = theme.colorScheme.secondary,
//						maxLines = 1,
//						overflow = TextOverflow.Ellipsis
//					)
//
//				}
//
//				IconButton(
//					modifier = Modifier,
//					onClick = {}
//				) {
//					Icon(
//						imageVector = Icons.Filled.MoreVertical,
//						contentDescription = "MoreVertical"
//					)
//				}
//
//			}
//
//		}

	}

}