package dev.drsn.karrots.innertube.parser.partial.chunk

import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
sealed class Menu

@Serializable
data class RadioMenuItem(
	val playlistId: String
) : Menu()

@Serializable
data class AlbumMenuItem(
	val browseId: String
) : Menu()

@Serializable
data class ArtistMenuItem(
	val browseId: String
) : Menu()

@Serializable
data class CreditMenuItem(
	val browseId: String
) : Menu()

/**
 * Provide __Object.menu__
 */
fun ChunkParser.parseMenu(obj: JsonElement?): List<Menu> {
	val menu = arrayListOf<Menu>()

	obj?.path("menuRenderer.items")?.jsonArray?.forEach {
		it.path("menuNavigationItemRenderer")?.let { navItem ->

			when (navItem.path("text.runs[0].text")?.maybeStringVal?.trim()?.lowercase()) {
				"start radio" -> menu.add(
					RadioMenuItem(
						playlistId = navItem.path("navigationEndpoint.watchEndpoint.playlistId")?.maybeStringVal
							?: navItem.path("navigationEndpoint.watchPlaylistEndpoint.playlistId")?.maybeStringVal
							?: return@forEach,
					)
				)

				"go to album" -> menu.add(
					AlbumMenuItem(
						browseId = navItem.path("navigationEndpoint.browseEndpoint.browseId")?.maybeStringVal
							?: return@forEach,
					)
				)

				"go to artist" -> menu.add(
					ArtistMenuItem(
						browseId = navItem.path("navigationEndpoint.browseEndpoint.browseId")?.maybeStringVal
							?: return@forEach,
					)
				)

				"view song credits" -> menu.add(
					CreditMenuItem(
						browseId = navItem.path("navigationEndpoint.browseEndpoint.browseId")?.maybeStringVal
							?: return@forEach,
					)
				)
			}
		}
	}

	return menu
}