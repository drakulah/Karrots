package dev.drsn.karrots.ui.router

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.drsn.karrots.LocalScreenRouter
import dev.drsn.karrots.ui.screen.InteractionScreen

@ExperimentalFoundationApi
@Composable
fun ScreenRouter(
	modifier: Modifier = Modifier
) {
	val screenRouter = LocalScreenRouter.current

	NavHost(
		modifier = modifier
			.fillMaxSize(),
		navController = screenRouter,
		startDestination = ScreenRoute.Interaction.path
	) {

		composable(
			route = ScreenRoute.Interaction.path
		) {
			InteractionScreen()
		}

	}

}