package dev.drsn.karrots.innertube.routes

import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.declare.Endpoint
import dev.drsn.karrots.innertube.declare.WebReqBodyWithBrowse
import dev.drsn.karrots.innertube.parser.Album
import dev.drsn.karrots.innertube.parser.ResponseParser
import dev.drsn.karrots.innertube.parser.parseAlbum
import dev.drsn.karrots.innertube.utils.runCatchingNonCancellable
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.album(browseId: String): Album? = runCatchingNonCancellable<Album?> {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		setBody(
			Json.encodeToString(WebReqBodyWithBrowse(browseId, this@album.webContext))
		)
	}.body()

	return ResponseParser.parseAlbum(res)
}