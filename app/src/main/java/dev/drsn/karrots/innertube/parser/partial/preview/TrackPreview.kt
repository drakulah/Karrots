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
import dev.drsn.karrots.innertube.utils.eatFiveStarDoNothing
import dev.drsn.karrots.innertube.utils.isDurationText
import dev.drsn.karrots.innertube.utils.isMaybeTitle
import dev.drsn.karrots.innertube.utils.isTrackPlays
import dev.drsn.karrots.innertube.utils.mixedJsonArray
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
data class TrackPreview(
	val id: String,
	val title: String,
	val durationText: String?,
	val trackPlays: String?,
	val album: AlbumBasicInfo?,
	val menu: List<Menu>,
	val uploaders: List<Uploader>,
	val thumbnails: List<ThumbnailInfo>
) : PreviewParser.ContentPreview()

fun PreviewParser.parseTrackPreview(obj: JsonElement?): TrackPreview? {

	var id: String? = null
	var title: String? = null
	var trackPlays: String? = null
	var durationText: String? = null
	var album: AlbumBasicInfo? = null

	val uploaders = arrayListOf<Uploader>()

	/************************************************/

	obj?.let { raw ->
		id = ChunkParser.parseId(
			raw.path("navigationEndpoint")
				?: raw.path("title.runs[0].navigationEndpoint")
		)
		title = raw.path("title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
		durationText = raw.path("lengthText.runs[0].text").maybeStringVal?.nullifyIfEmpty()

		mixedJsonArray(
			raw.path("subtitle.runs"),
			raw.path("lengthText.runs"),
			raw.path("secondTitle.runs"),
			raw.path("longBylineText.runs"),
			raw.path("shortBylineText.runs"),
		).forEach {

			val tempText = it.path("text").maybeStringVal.nullifyIfEmpty() ?: return@forEach

			when (val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))) {
				ItemType.ArtistPreview, ItemType.UserChannelPreview -> {
					uploaders.add(
						Uploader(
							title = tempText,
							isArtist = tempType == ItemType.ArtistPreview,
							browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
						)
					)
				}

				else -> when {
					tempText.isDurationText() -> durationText = tempText
					tempText.isTrackPlays() -> trackPlays = tempText
					tempText.isMaybeTitle() -> uploaders.add(
						Uploader(
							title = tempText,
							isArtist = false,
							browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
						)
					)

					else -> eatFiveStarDoNothing()
				}
			}
		}
	}

	/************************************************/

	mixedJsonArray(
		obj.path("flexColumns"),
		obj.path("fixedColumns")
	).forEach { innerRaw ->

		(innerRaw.path("musicResponsiveListItemFlexColumnRenderer.text.runs")
			?: innerRaw.path("musicResponsiveListItemFixedColumnRenderer.text.runs"))
			?.jsonArray
			?.forEach {
				val tempText = it.path("text").maybeStringVal.nullifyIfEmpty() ?: return@forEach

				when (val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))) {
					ItemType.Song, ItemType.Video, ItemType.Podcast -> {
						title = tempText
						ChunkParser.parseId(it.path("navigationEndpoint"))?.let { e -> id = e }
					}

					ItemType.ArtistPreview, ItemType.UserChannelPreview -> {
						uploaders.add(
							Uploader(
								title = tempText,
								isArtist = tempType == ItemType.ArtistPreview,
								browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
							)
						)
					}

					ItemType.AlbumPreview -> {
						album = AlbumBasicInfo(
							title = tempText,
							browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
						)
					}

					else -> when {
						tempText.isDurationText() -> durationText = tempText
						tempText.isTrackPlays() -> trackPlays = tempText
						tempText.isMaybeTitle() -> uploaders.add(
							Uploader(
								title = tempText,
								isArtist = false,
								browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
							)
						)

						else -> eatFiveStarDoNothing()
					}
				}
			}

	}

	/************************************************/

	return TrackPreview(
		id = id ?: return null,
		title = title ?: return null,
		trackPlays = trackPlays,
		durationText = durationText,
		album = album,
		uploaders = uploaders,
		thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnail") ?: obj.path("thumbnailRenderer")),
		menu = ChunkParser.parseMenu(obj.path("menu") ?: obj.path("musicTwoRowItemRenderer.menu")),
	)
}