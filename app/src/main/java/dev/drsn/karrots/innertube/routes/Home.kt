package dev.drsn.karrots.innertube.routes

import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.declare.Endpoint
import dev.drsn.karrots.innertube.declare.WebReqBody
import dev.drsn.karrots.innertube.declare.WebReqBodyWithBrowse
import dev.drsn.karrots.innertube.parser.Home
import dev.drsn.karrots.innertube.parser.ResponseParser
import dev.drsn.karrots.innertube.parser.parseHome
import dev.drsn.karrots.innertube.utils.runCatchingNonCancellable
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.home(continuation: String? = null): Home? = runCatchingNonCancellable<Home?> {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		if (continuation == null) {
			setBody(
				Json.encodeToString(WebReqBodyWithBrowse("FEmusic_home", this@home.webContext))
			)
		} else {
			url {
				parameter("type", "next")
				parameter("ctoken", continuation)
				parameter("continuation", continuation)
			}
			setBody(
				Json.encodeToString(WebReqBody(this@home.webContext))
			)
		}
	}.body()

	return ResponseParser.parseHome(res)
}