package dev.drsn.karrots.innertube.routes

import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.declare.Endpoint
import dev.drsn.karrots.innertube.declare.WebReqBodyWithVidListId
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.upNext(videoId: String, playlistId: String) {
	val res: JsonElement = this.webHttpClient.post(Endpoint.next) {
		setBody(
			Json.encodeToString(WebReqBodyWithVidListId(videoId, playlistId, this@upNext.webContext))
		)
	}.body()
}