package dev.drsn.karrots.innertube.parser.partial.preview

import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import dev.drsn.karrots.innertube.parser.partial.chunk.ChunkParser
import dev.drsn.karrots.innertube.parser.partial.chunk.ItemType
import dev.drsn.karrots.innertube.parser.partial.chunk.Menu
import dev.drsn.karrots.innertube.parser.partial.chunk.ThumbnailInfo
import dev.drsn.karrots.innertube.parser.partial.chunk.parseId
import dev.drsn.karrots.innertube.parser.partial.chunk.parseItemType
import dev.drsn.karrots.innertube.parser.partial.chunk.parseMenu
import dev.drsn.karrots.innertube.parser.partial.chunk.parseThumbnail
import dev.drsn.karrots.innertube.utils.isTrackCount
import dev.drsn.karrots.innertube.utils.mixedJsonArray
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PlaylistPreview(
	val title: String,
	val browseId: String,
	val menu: List<Menu>,
	val trackCount: String?,
	val thumbnails: List<ThumbnailInfo>
) : PreviewParser.ContentPreview()

fun PreviewParser.parsePlaylistPreview(obj: JsonElement?): PlaylistPreview? {

	val uploaders = arrayListOf<Uploader>()

	val title = (obj.path("title.runs[0].text")
		?: obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].text")
			).maybeStringVal?.nullifyIfEmpty() ?: return null

	val browseId = ChunkParser.parseId(
		obj.path("title.runs[0].navigationEndpoint") ?: obj.path("navigationEndpoint")
	) ?: return null

	val menu = ChunkParser.parseMenu(obj.path("menu"))
	val thumbnails =
		ChunkParser.parseThumbnail(obj.path("thumbnailRenderer") ?: obj.path("thumbnail"))

	var trackCount: String? = null

	mixedJsonArray(
		obj.path("subtitle.runs"),
		obj.path("secondTitle.runs"),
		obj.path("flexColumns[1].musicResponsiveListItemFlexColumnRenderer.text.runs")
	).forEach {
		val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))
		val tempText = it.path("text")?.maybeStringVal?.nullifyIfEmpty() ?: return@forEach

		when (tempType) {
			ItemType.ArtistPreview, ItemType.UserChannelPreview -> {
				uploaders.add(
					Uploader(
						title = tempText,
						isArtist = tempType == ItemType.ArtistPreview,
						browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
					)
				)
			}

			else -> {
				if (tempText.isTrackCount()) trackCount = tempText
			}
		}
	}

	return PlaylistPreview(
		title = title,
		browseId = browseId,
		trackCount = trackCount,
		menu = menu,
		thumbnails = thumbnails
	)
}