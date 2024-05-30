package dev.drsn.karrots.ui.container

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.drsn.karrots.util.decodeComponent
import dev.drsn.karrots.util.encodeComponent

const val CONTAINER_ROUTE_HOME = "container_home"
const val CONTAINER_ROUTE_EXPLORE = "container_explore"
const val CONTAINER_ROUTE_SEARCH_AUTOCOMPLETE = "container_search_autocomplete"
const val CONTAINER_ROUTE_SEARCH = "container_search"
const val CONTAINER_ROUTE_ALBUM = "container_album"
const val CONTAINER_ROUTE_ARTIST = "container_artist"
const val CONTAINER_ROUTE_PLAYLIST = "container_playlist"

fun String?.isRouteContainerHome(): Boolean {
	val route = CONTAINER_ROUTE_HOME
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isRouteContainerExplore(): Boolean {
	val route = CONTAINER_ROUTE_EXPLORE
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isRouteContainerSearch(): Boolean {
	val route = CONTAINER_ROUTE_SEARCH
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isRouteContainerSearchAutocomplete(): Boolean {
	val route = CONTAINER_ROUTE_SEARCH_AUTOCOMPLETE
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isRouteContainerAlbum(): Boolean {
	val route = CONTAINER_ROUTE_ALBUM
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isRouteContainerArtist(): Boolean {
	val route = CONTAINER_ROUTE_ARTIST
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

fun String?.isRouteContainerPlaylist(): Boolean {
	val route = CONTAINER_ROUTE_PLAYLIST
	if (this.isNullOrEmpty() || this.length < route.length) return false
	val partialString = this.slice(route.indices).lowercase()
	return partialString == route.lowercase()
}

sealed class ContainerRoute(val path: String) {
	data object Home : ContainerRoute(CONTAINER_ROUTE_HOME)
	data object Explore : ContainerRoute(CONTAINER_ROUTE_EXPLORE)

	data object Search : ContainerRoute("$CONTAINER_ROUTE_SEARCH/{query}") {
		fun query(query: String) = "$CONTAINER_ROUTE_SEARCH/${query.encodeComponent()}"
	}

	data object SearchAutocomplete : ContainerRoute("$CONTAINER_ROUTE_SEARCH_AUTOCOMPLETE/{query}") {
		fun query(query: String) = "$CONTAINER_ROUTE_SEARCH_AUTOCOMPLETE/${query.encodeComponent()}"
	}

	data object Album : ContainerRoute("$CONTAINER_ROUTE_ALBUM/{id}") {
		fun browseId(id: String) = "$CONTAINER_ROUTE_ALBUM/${id.encodeComponent()}"
	}

	data object Artist : ContainerRoute("$CONTAINER_ROUTE_ARTIST/{id}") {
		fun browseId(id: String) = "$CONTAINER_ROUTE_ARTIST/${id.encodeComponent()}"
	}

	data object Playlist : ContainerRoute("$CONTAINER_ROUTE_PLAYLIST/{id}") {
		fun browseId(id: String) = "$CONTAINER_ROUTE_PLAYLIST/${id.encodeComponent()}"
	}
}

@ExperimentalFoundationApi
@Composable
fun ContainerRouter(
	modifier: Modifier = Modifier,
	controller: NavHostController,
	startDestination: String,
) {

	NavHost(
		modifier = modifier
			.fillMaxSize(),
		navController = controller,
		startDestination = startDestination
	) {

		composable(
			route = ContainerRoute.Home.path
		) {
			HomeContainer(
				controller = controller
			)
		}

		composable(
			route = ContainerRoute.Explore.path
		) {
			ExploreContainer(
				controller = controller
			)
		}

		composable(
			route = ContainerRoute.SearchAutocomplete.path
		) { backStackEntry ->
			val query = backStackEntry.arguments?.getString("query")

//			return@composable controller.navigate(ContainerRoute.Search.query("Gorkhe khukuri"))

			SearchContainerAC(
				controller = controller,
				query = query?.decodeComponent()
			)
		}

		composable(
			route = ContainerRoute.Search.path
		) { backStackEntry ->
			val query = backStackEntry.arguments?.getString("query")

			SearchContainer(
				controller = controller,
				query = query?.decodeComponent()
			)
		}

		composable(
			route = ContainerRoute.Album.path,
		) { backStackEntry ->
			val albumId = backStackEntry.arguments?.getString("id")

			requireNotNull(albumId) {
				"Album Id is required"
			}

			AlbumContainer(
				browseId = albumId.decodeComponent(),
				controller = controller
			)
		}

		composable(
			route = ContainerRoute.Artist.path,
		) { backStackEntry ->
			val artistId = backStackEntry.arguments?.getString("id")

			requireNotNull(artistId) {
				"Artist Id is required"
			}

			ArtistContainer(
				browseId = artistId.decodeComponent(),
				controller = controller
			)
		}

		composable(
			route = ContainerRoute.Playlist.path,
		) { backStackEntry ->
			val playlistId = backStackEntry.arguments?.getString("id")

			requireNotNull(playlistId) {
				"Playlist Id is required"
			}

			PlaylistContainer(
				browseId = playlistId.decodeComponent(),
				controller = controller
			)
		}

	}

}