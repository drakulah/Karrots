package dev.drsn.karrots.innertube.parser.partial.chunk

import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import kotlinx.serialization.json.JsonElement

/**
 * Provide __Object.continuation__
 */
fun ChunkParser.parseContinuation(obj: JsonElement?): String? {
	return (obj?.path("nextContinuationData.continuation")
		?: obj?.path("nextRadioContinuationData.continuation"))?.maybeStringVal
}