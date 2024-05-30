package dev.drsn.karrots.ui.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.sharp.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.drsn.karrots.ui.component.SearchField

@Composable
fun SearchContainerAC(
	controller: NavHostController,
	query: String? = null
) {

	var userQuery by remember { mutableStateOf(query ?: "") }
	var isSearchFieldActive by remember { mutableStateOf(true) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.statusBarsPadding()
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.statusBarsPadding()
//				.padding(end = 16.dp)
				.padding(vertical = 3.dp),
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

//			if (query.isNotEmpty() && isActive) IconButton(
//				onClick = { onQueryChange("") }
//			) {
//				Icon(
//					imageVector = Icons.Sharp.Clear,
//					contentDescription = "Clear"
//				)
//			}
		}

		LazyColumn(
			modifier = Modifier
				.weight(1f)
		) {

		}

	}
}