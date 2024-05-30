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
import dev.drsn.karrots.innertube.utils.isAlbumType
import dev.drsn.karrots.innertube.utils.isYearText
import dev.drsn.karrots.innertube.utils.mixedJsonArray
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class AlbumType {
	Single,
	EP,
	Album
}

fun String?.toAlbumType(): AlbumType {
	return when (this?.uppercase()) {
		"EP" -> AlbumType.EP
		"SINGLE" -> AlbumType.Single
		else -> AlbumType.Album
	}
}

@Serializable
data class AlbumBasicInfo(
	val title: String,
	val browseId: String?
)

@Serializable
data class AlbumPreview(
	val year: String?,
	val title: String,
	val browseId: String,
	val albumType: AlbumType,
	val menu: List<Menu>,
	val uploaders: List<Uploader>,
	val thumbnails: List<ThumbnailInfo>
) : PreviewParser.ContentPreview()

fun PreviewParser.parseAlbumPreview(obj: JsonElement?): AlbumPreview? {
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

	var albumType = AlbumType.Album
	var yearText: String? = null

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
				when {
					tempText.isYearText() -> yearText = tempText
					tempText.isAlbumType() -> albumType = tempText.toAlbumType()
				}
			}
		}
	}

	return AlbumPreview(
		year = yearText,
		title = title,
		browseId = browseId,
		albumType = albumType,
		menu = menu,
		uploaders = uploaders,
		thumbnails = thumbnails
	)
}