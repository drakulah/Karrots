package dev.drsn.karrots.innertube.routes

import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.declare.Endpoint
import dev.drsn.karrots.innertube.declare.WebReqBody
import dev.drsn.karrots.innertube.declare.WebReqBodyWithBrowse
import dev.drsn.karrots.innertube.parser.Playlist
import dev.drsn.karrots.innertube.parser.ResponseParser
import dev.drsn.karrots.innertube.parser.parsePlaylist
import dev.drsn.karrots.innertube.utils.runCatchingNonCancellable
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.playlist(browseId: String? = null, continuation: String? = null): Playlist? =
	runCatchingNonCancellable<Playlist?> {
		val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
			if (continuation == null && browseId != null) {
				setBody(
					Json.encodeToString(WebReqBodyWithBrowse(browseId, this@playlist.webContext))
				)
			} else if (continuation != null && browseId == null) {
				url {
					parameter("type", "next")
					parameter("ctoken", continuation)
					parameter("continuation", continuation)
				}
				setBody(
					Json.encodeToString(WebReqBody(this@playlist.webContext))
				)
			} else {
				return null
			}
		}.body()

		return ResponseParser.parsePlaylist(res)
	}