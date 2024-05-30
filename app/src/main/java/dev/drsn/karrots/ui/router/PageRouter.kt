package dev.drsn.karrots.ui.router

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.drsn.karrots.LocalPageRouter
import dev.drsn.karrots.ui.page.ExplorePage
import dev.drsn.karrots.ui.page.HomePage
import dev.drsn.karrots.ui.page.SearchPage
import dev.drsn.karrots.ui.page.library.LibraryPage

const val PAGE_ROUTE_HOME_PATH = "home"
const val PAGE_ROUTE_SEARCH_PATH = "search"
const val PAGE_ROUTE_EXPLORE_PATH = "explore"
const val PAGE_ROUTE_LIBRARY_PATH = "library"

fun String?.isPageRouteHome(): Boolean {
	val route = PAGE_ROUTE_HOME_PATH
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isPageRouteSearch(): Boolean {
	val route = PAGE_ROUTE_SEARCH_PATH
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isPageRouteExplore(): Boolean {
	val route = PAGE_ROUTE_EXPLORE_PATH
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isPageRouteLibrary(): Boolean {
	val route = PAGE_ROUTE_LIBRARY_PATH
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

sealed class PageRoute(val path: String) {
	data object Home : PageRoute(PAGE_ROUTE_HOME_PATH)
	data object Search : PageRoute(PAGE_ROUTE_SEARCH_PATH)
	data object Explore : PageRoute(PAGE_ROUTE_EXPLORE_PATH)
	data object Library : PageRoute(PAGE_ROUTE_LIBRARY_PATH)
}

@ExperimentalFoundationApi
@Composable
fun PageRouter(
	modifier: Modifier = Modifier
) {

	val pageRouter = LocalPageRouter.current

	NavHost(
		modifier = modifier
			.fillMaxSize(),
		navController = pageRouter,
		startDestination = PageRoute.Home.path
	) {

		composable(
			route = PageRoute.Home.path,
		) {
			HomePage()
		}

		composable(
			route = PageRoute.Search.path,
		) {
			SearchPage()
		}

		composable(
			route = PageRoute.Explore.path,
		) {
			ExplorePage()
		}

		composable(
			route = PageRoute.Library.path,
		) {
			LibraryPage()
		}

	}

}