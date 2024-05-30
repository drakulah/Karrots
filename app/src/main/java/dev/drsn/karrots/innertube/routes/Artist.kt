package dev.drsn.karrots.innertube.routes

import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.declare.Endpoint
import dev.drsn.karrots.innertube.declare.WebReqBodyWithBrowse
import dev.drsn.karrots.innertube.parser.Artist
import dev.drsn.karrots.innertube.parser.ResponseParser
import dev.drsn.karrots.innertube.parser.parseArtist
import dev.drsn.karrots.innertube.utils.runCatchingNonCancellable
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.artist(browseId: String): Artist? = runCatchingNonCancellable<Artist?> {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		setBody(
			Json.encodeToString(WebReqBodyWithBrowse(browseId, this@artist.webContext))
		)
	}.body()

	return ResponseParser.parseArtist(res)
}