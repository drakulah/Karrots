package dev.drsn.karrots.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Explore
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.LibraryMusic
import androidx.compose.material.icons.sharp.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.drsn.karrots.LocalPageRouter
import dev.drsn.karrots.ui.component.BottomNavigationBar
import dev.drsn.karrots.ui.component.BottomNavigationBarItem
import dev.drsn.karrots.ui.router.PageRoute
import dev.drsn.karrots.ui.router.isPageRouteExplore
import dev.drsn.karrots.ui.router.isPageRouteHome
import dev.drsn.karrots.ui.router.isPageRouteLibrary
import dev.drsn.karrots.ui.router.isPageRouteSearch

@Composable
fun BottomNavigation(
	modifier: Modifier = Modifier,
) {

	BottomNavigationBar(
		modifier = modifier
	) {

		val pageRouter = LocalPageRouter.current
		val currentRoute = pageRouter.currentBackStackEntryAsState().value?.destination?.route

		BottomNavigationBarItem(
			modifier = Modifier
				.weight(1f / 4f),
			label = "Home",
			imageVector = Icons.Sharp.Home,
			selected = currentRoute.isPageRouteHome(),
			onClick = { pageRouter.navigate(PageRoute.Home.path) }
		)

		BottomNavigationBarItem(
			modifier = Modifier
				.weight(1f / 4f),
			label = "Search",
			imageVector = Icons.Sharp.Search,
			selected = currentRoute.isPageRouteSearch(),
			onClick = { pageRouter.navigate(PageRoute.Search.path) }
		)

		BottomNavigationBarItem(
			modifier = Modifier
				.weight(1f / 4f),
			label = "Explore",
			imageVector = Icons.Sharp.Explore,
			selected = currentRoute.isPageRouteExplore(),
			onClick = { pageRouter.navigate(PageRoute.Explore.path) }
		)

		BottomNavigationBarItem(
			modifier = Modifier
				.weight(1f / 4f),
			label = "Library",
			imageVector = Icons.Sharp.LibraryMusic,
			selected = currentRoute.isPageRouteLibrary(),
			onClick = { pageRouter.navigate(PageRoute.Library.path) }
		)
	}
}