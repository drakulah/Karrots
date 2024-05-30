package dev.drsn.karrots.innertube.parser.partial.chunk

import dev.drsn.karrots.innertube.json.maybeIntVal
import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
data class ThumbnailInfo(
	val url: String,
	val width: Int,
	val height: Int
)

/**
 * Provide __Object.background__ or __Object.thumbnail__ or __Object.thumbnailRenderer__
 */
fun ChunkParser.parseThumbnail(obj: JsonElement?): List<ThumbnailInfo> {
	val thumbnailArr = arrayListOf<ThumbnailInfo>()

	(
			obj?.path("thumbnails") ?: obj?.path("musicThumbnailRenderer.thumbnail.thumbnails")
			?: obj?.path("croppedSquareThumbnailRenderer.thumbnail.thumbnails")
			)?.jsonArray?.forEach {
			thumbnailArr.add(
				ThumbnailInfo(
					url = it.path("url")?.maybeStringVal ?: return@forEach,
					width = it.path("width")?.maybeIntVal ?: return@forEach,
					height = it.path("height")?.maybeIntVal ?: return@forEach
				)
			)
		}

	return thumbnailArr
}