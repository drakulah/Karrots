package dev.drsn.karrots.innertube.routes

import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.declare.AndroidReqBodyPlayer
import dev.drsn.karrots.innertube.declare.Endpoint
import dev.drsn.karrots.innertube.parser.PlayerResponse
import dev.drsn.karrots.innertube.parser.ResponseParser
import dev.drsn.karrots.innertube.parser.parsePlayer
import dev.drsn.karrots.innertube.utils.runCatchingNonCancellable
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.player(videoId: String): PlayerResponse? =
	runCatchingNonCancellable<PlayerResponse?> {
		val res: JsonElement = this.androidHttpClient.post(Endpoint.player) {
			setBody(
				Json.encodeToString(AndroidReqBodyPlayer(videoId, this@player.androidContext))
			)
		}.body()

		return ResponseParser.parsePlayer(res)
	}