package dev.drsn.karrots.innertube.parser

import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import dev.drsn.karrots.innertube.parser.partial.chunk.ChunkParser
import dev.drsn.karrots.innertube.parser.partial.chunk.ItemType
import dev.drsn.karrots.innertube.parser.partial.chunk.Menu
import dev.drsn.karrots.innertube.parser.partial.chunk.ThumbnailInfo
import dev.drsn.karrots.innertube.parser.partial.chunk.parseContinuation
import dev.drsn.karrots.innertube.parser.partial.chunk.parseId
import dev.drsn.karrots.innertube.parser.partial.chunk.parseItemType
import dev.drsn.karrots.innertube.parser.partial.chunk.parseMenu
import dev.drsn.karrots.innertube.parser.partial.chunk.parseThumbnail
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.parser.partial.preview.Uploader
import dev.drsn.karrots.innertube.parser.partial.preview.parseAlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parsePlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseTrackPreview
import dev.drsn.karrots.innertube.utils.eatFiveStarDoNothing
import dev.drsn.karrots.innertube.utils.isPlaylistDuration
import dev.drsn.karrots.innertube.utils.isTrackCount
import dev.drsn.karrots.innertube.utils.isTrackPlays
import dev.drsn.karrots.innertube.utils.isYearText
import dev.drsn.karrots.innertube.utils.mixedJsonArray
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@Serializable
data class PlaylistListTopic(
	val title: String,
	val subtitle: String?,
	val browseId: String?,
	val thumbnail: List<ThumbnailInfo>
)

@Serializable
data class PlaylistListContainer(
	val topic: PlaylistListTopic,
	val preContents: List<PreviewParser.ContentPreview>
)

@Serializable
sealed class Playlist

@Serializable
data class PlaylistInfo(
	val title: String,
	val description: String?,
	val yearText: String?,
	val trackCount: String?,
	val viewCount: String?,
	val menu: List<Menu>,
	val playlistDuration: String?,
	val uploaders: List<Uploader>,
	val thumbnail: List<ThumbnailInfo>,
	val track: List<TrackPreview>,
	val tracksContinuation: String?,
	val others: List<PlaylistListContainer>,
	val othersContinuation: String?,
) : Playlist()

@Serializable
data class PlaylistContinuation(
	val track: List<TrackPreview>,
	val tracksContinuation: String?,
	val others: List<PlaylistListContainer>
) : Playlist()

fun ResponseParser.parsePlaylist(obj: JsonElement?): Playlist? {

	val track = arrayListOf<TrackPreview>()
	val others = arrayListOf<PlaylistListContainer>()
	val uploaders = arrayListOf<Uploader>()

	var yearText: String? = null
	var viewCount: String? = null
	var trackCount: String? = null
	var playlistDuration: String? = null
	var tracksContinuation: String? = null

	val title =
		obj.path("header.musicDetailHeaderRenderer.title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
	val description =
		obj.path("header.musicDetailHeaderRenderer.description.runs[0].text").maybeStringVal?.nullifyIfEmpty()
	val menu = ChunkParser.parseMenu(obj.path("header.musicDetailHeaderRenderer.menu"))
	val thumbnail = ChunkParser.parseThumbnail(obj.path("header.musicDetailHeaderRenderer.thumbnail"))
	val othersContinuation =
		ChunkParser.parseContinuation(obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer.continuations[0]"))

	mixedJsonArray(
		obj.path("header.musicDetailHeaderRenderer.subtitle.runs"),
		obj.path("header.musicDetailHeaderRenderer.secondSubtitle.runs"),
	).forEach {

		val tempText = it.path("text").maybeStringVal.nullifyIfEmpty() ?: return@forEach

		when (val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))) {
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
					tempText.isTrackPlays() -> viewCount = tempText
					tempText.isTrackCount() -> trackCount = tempText
					tempText.isPlaylistDuration() -> playlistDuration = tempText
				}
			}
		}
	}

	val tempJsonArr = arrayListOf<JsonElement>()
	obj.path("continuationContents")?.also(tempJsonArr::add)

	(obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer.contents")
		?: obj.path("continuationContents.sectionListContinuation.contents")
		?: JsonArray(tempJsonArr))
		.jsonArray
		.forEach { eachComp ->

			val preContents = arrayListOf<PreviewParser.ContentPreview>()

			(eachComp.path("musicCarouselShelfRenderer") ?: eachComp.path("musicPlaylistShelfRenderer")
			?: eachComp.path("musicPlaylistShelfContinuation") ?: eachComp.path("musicShelfRenderer"))
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

					tracksContinuation =
						ChunkParser.parseContinuation(sharedContainer.path("continuations[0]"))

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

							when (itemType) {
								ItemType.Video, ItemType.Song, ItemType.Podcast -> track.add(
									PreviewParser.parseTrackPreview(itemRenderer) ?: return@forEach
								)

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

					if (topicTitle != null && preContents.isNotEmpty()) others.add(
						PlaylistListContainer(
							PlaylistListTopic(
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

	return if (title.isNullOrEmpty()) PlaylistContinuation(
		tracksContinuation = tracksContinuation,
		track = track,
		others = others
	) else PlaylistInfo(
		title = title,
		description = description,
		yearText = yearText,
		trackCount = trackCount,
		viewCount = viewCount,
		playlistDuration = playlistDuration,
		uploaders = uploaders,
		thumbnail = thumbnail,
		menu = menu,
		track = track,
		others = others,
		othersContinuation = othersContinuation,
		tracksContinuation = tracksContinuation
	)
}