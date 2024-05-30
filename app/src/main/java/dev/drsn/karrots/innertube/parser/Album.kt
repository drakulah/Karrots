package dev.drsn.karrots.innertube.parser

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
import dev.drsn.karrots.innertube.parser.partial.preview.AlbumType
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.parser.partial.preview.Uploader
import dev.drsn.karrots.innertube.parser.partial.preview.parseAlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parsePlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseTrackPreview
import dev.drsn.karrots.innertube.parser.partial.preview.toAlbumType
import dev.drsn.karrots.innertube.utils.eatFiveStarDoNothing
import dev.drsn.karrots.innertube.utils.isAlbumType
import dev.drsn.karrots.innertube.utils.isPlaylistDuration
import dev.drsn.karrots.innertube.utils.isTrackCount
import dev.drsn.karrots.innertube.utils.isYearText
import dev.drsn.karrots.innertube.utils.mixedJsonArray
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@Serializable
data class AlbumListTopic(
	val title: String,
	val subtitle: String?,
	val browseId: String?,
	val thumbnail: List<ThumbnailInfo>
)

@Serializable
data class AlbumListContainer(
	val topic: AlbumListTopic,
	val preContents: List<PreviewParser.ContentPreview>
)

@Serializable
data class Album(
	val title: String,
	val yearText: String?,
	val trackCount: String?,
	val description: String?,
	val albumType: AlbumType,
	val albumDuration: String?,
	val uploaders: List<Uploader>,
	val thumbnail: List<ThumbnailInfo>,
	val menu: List<Menu>,
	val track: List<TrackPreview>,
	val others: List<AlbumListContainer>
)

fun ResponseParser.parseAlbum(obj: JsonElement?): Album? {

	val track = arrayListOf<TrackPreview>()
	val others = arrayListOf<AlbumListContainer>()
	val uploaders = arrayListOf<Uploader>()

	var yearText: String? = null
	var trackCount: String? = null
	var albumDuration: String? = null
	var albumType: AlbumType = AlbumType.Album

	val title =
		obj.path("header.musicDetailHeaderRenderer.title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
			?: return null
	val description =
		obj.path("header.musicDetailHeaderRenderer.description.runs[0].text").maybeStringVal?.nullifyIfEmpty()
	val menu = ChunkParser.parseMenu(obj.path("header.musicDetailHeaderRenderer.menu"))
	val thumbnail = ChunkParser.parseThumbnail(obj.path("header.musicDetailHeaderRenderer.thumbnail"))

	mixedJsonArray(
		obj.path("header.musicDetailHeaderRenderer.subtitle.runs"),
		obj.path("header.musicDetailHeaderRenderer.secondSubtitle.runs"),
	).forEach {

		val tempText = it.path("text").maybeStringVal.nullifyIfEmpty() ?: return@forEach
		val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))

		when (tempType) {
			ItemType.ArtistPreview, ItemType.UserChannelPreview -> {
				if (tempText.isEmpty()) return@forEach
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
					tempText.isTrackCount() -> trackCount = tempText
					tempText.isPlaylistDuration() -> albumDuration = tempText
					tempText.isAlbumType() -> albumType = tempText.toAlbumType()
				}
			}
		}
	}

	obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer.contents")
		?.jsonArray
		?.forEachIndexed { index, eachComp ->

			val preContents = arrayListOf<PreviewParser.ContentPreview>()

			(eachComp.path("musicCarouselShelfRenderer") ?: eachComp.path("musicShelfRenderer"))
				?.jsonObject
				?.let { sharedContainer ->

					val topicTitle =
						sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer.title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
					val topicSubtitle =
						sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer.strapline.runs[0].text").maybeStringVal?.nullifyIfEmpty()
					val topicBrowseId =
						ChunkParser.parseId(sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer.title.runs[0].navigationEndpoint"))
					val topicThumbnail =
						ChunkParser.parseThumbnail(sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer.thumbnail"))

					if (topicTitle == null && index != 0) return@forEachIndexed

					sharedContainer.path("contents")
						?.jsonArray
						?.forEach { eachItem ->

							val itemRenderer =
								eachItem.path("musicTwoRowItemRenderer")
									?: eachItem.path("musicResponsiveListItemRenderer")
							val itemType = ChunkParser.parseItemType(
								eachItem.path("musicTwoRowItemRenderer.navigationEndpoint")
									?: eachItem.path("musicResponsiveListItemRenderer.flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].navigationEndpoint")
							)

							when (index) {
								0 -> {
									when (itemType) {
										ItemType.Video, ItemType.Song -> track.add(
											PreviewParser.parseTrackPreview(itemRenderer) ?: return@forEach
										)

										else -> eatFiveStarDoNothing()
									}
								}

								else -> {
									when (itemType) {
										ItemType.AlbumPreview -> preContents.add(
											PreviewParser.parseAlbumPreview(itemRenderer) ?: return@forEach
										)

										ItemType.ArtistPreview -> preContents.add(
											PreviewParser.parseArtistPreview(itemRenderer) ?: return@forEach
										)

										ItemType.PlaylistPreview -> preContents.add(
											PreviewParser.parsePlaylistPreview(itemRenderer) ?: return@forEach
										)

										else -> eatFiveStarDoNothing()
									}
								}
							}

						}

					if (index != 0 && topicTitle != null && preContents.isNotEmpty()) others.add(
						AlbumListContainer(
							AlbumListTopic(
								title = topicTitle,
								subtitle = topicSubtitle,
								browseId = topicBrowseId,
								thumbnail = topicThumbnail
							),
							preContents
						)
					)
				}
		}

	if (title.isEmpty()) return null

	return Album(
		title = title,
		description = description,
		yearText = yearText,
		trackCount = trackCount,
		albumType = albumType,
		albumDuration = albumDuration,
		uploaders = uploaders,
		thumbnail = thumbnail,
		menu = menu,
		track = track,
		others = others
	)
}