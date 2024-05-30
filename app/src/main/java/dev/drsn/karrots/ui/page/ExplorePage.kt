package dev.drsn.karrots.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import dev.drsn.karrots.LocalContentLoader
import dev.drsn.karrots.ui.container.ContainerRoute
import dev.drsn.karrots.ui.container.ContainerRouter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExplorePage() {
	val contentLoader = LocalContentLoader.current!!
	val navController = rememberNavController()

	contentLoader.explore.cleanLoad(true)

	ContainerRouter(
		controller = navController,
		startDestination = ContainerRoute.Explore.path
	)
}