package dev.drsn.karrots.innertube.routes

import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.declare.Endpoint
import dev.drsn.karrots.innertube.declare.WebReqBody
import dev.drsn.karrots.innertube.declare.WebReqBodyWithQuery
import dev.drsn.karrots.innertube.declare.WebReqBodyWithQueryAndParams
import dev.drsn.karrots.innertube.parser.ResponseParser
import dev.drsn.karrots.innertube.parser.Search
import dev.drsn.karrots.innertube.parser.parseSearch
import dev.drsn.karrots.innertube.utils.runCatchingNonCancellable
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.search(
	query: String? = null,
	continuation: String? = null,
	params: String? = null
): Search? =
	runCatchingNonCancellable<Search?> {
		val res: JsonElement = this.webHttpClient.post(Endpoint.search) {
			if (continuation == null && query != null) {
				if (params == null)
					setBody(
						Json.encodeToString(WebReqBodyWithQuery(query, this@search.webContext))
					)
				else
					setBody(
						Json.encodeToString(WebReqBodyWithQueryAndParams(query, params, this@search.webContext))
					)
			} else if (continuation != null && query == null) {
				url {
					parameter("type", "next")
					parameter("ctoken", continuation)
					parameter("continuation", continuation)
				}
				setBody(
					Json.encodeToString(WebReqBody(this@search.webContext))
				)
			} else {
				return null
			}
		}.body()

		return ResponseParser.parseSearch(res)
	}