package dev.drsn.karrots.innertube.parser.partial.chunk

import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.json.JsonElement

/**
 * Provide __Object.navigationEndpoint__
 */
fun ChunkParser.parseId(obj: JsonElement?): String? {
	return (obj.path("watchEndpoint.videoId").maybeStringVal?.nullifyIfEmpty()
		?: obj.path("browseEndpoint.browseId").maybeStringVal?.nullifyIfEmpty())
}