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
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.parseAlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parsePlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseTrackPreview
import dev.drsn.karrots.innertube.utils.eatFiveStarDoNothing
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@Serializable
data class ArtistListTopic(
	val title: String,
	val subtitle: String?,
	val browseId: String?,
	val thumbnail: List<ThumbnailInfo>
)

@Serializable
data class ArtistListContainer(
	val topic: ArtistListTopic,
	val preContents: List<PreviewParser.ContentPreview>
)

@Serializable
data class Artist(
	val title: String,
	val description: String?,
	val subscribers: String,
	val thumbnail: List<ThumbnailInfo>,
	val menu: List<Menu>,
	val others: List<ArtistListContainer>
)

fun ResponseParser.parseArtist(obj: JsonElement?): Artist? {

	val others = arrayListOf<ArtistListContainer>()

	val title =
		obj.path("header.musicImmersiveHeaderRenderer.title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
			?: return null
	val subscribers =
		obj.path("header.musicImmersiveHeaderRenderer.subscriptionButton.subscribeButtonRenderer.subscriberCountText.runs[0].text").maybeStringVal?.nullifyIfEmpty()
			?: return null
	val description =
		obj.path("header.musicImmersiveHeaderRenderer.description.runs[0].text").maybeStringVal?.nullifyIfEmpty()
	val menu = ChunkParser.parseMenu(obj.path("header.musicDetailHeaderRenderer.menu"))
	val thumbnail =
		ChunkParser.parseThumbnail(obj.path("header.musicImmersiveHeaderRenderer.thumbnail"))

	obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer.contents")
		?.jsonArray
		?.forEach { eachComp ->

			val preContents = arrayListOf<PreviewParser.ContentPreview>()

			(eachComp.path("musicCarouselShelfRenderer") ?: eachComp.path("musicShelfRenderer"))
				?.jsonObject
				?.let { sharedContainer ->

					var topicTitle: String
					var topicSubtitle: String?
					var topicBrowseId: String?
					var topicThumbnail: List<ThumbnailInfo>

					(sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer")
						?: (if (sharedContainer.path("title") != null) sharedContainer else null)).let { sharedHeader ->
						topicTitle = sharedHeader.path("title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
							?: return@forEach
						topicSubtitle =
							sharedHeader.path("strapline.runs[0].text").maybeStringVal?.nullifyIfEmpty()
						topicBrowseId =
							ChunkParser.parseId(sharedHeader.path("title.runs[0].navigationEndpoint"))
						topicThumbnail = ChunkParser.parseThumbnail(sharedHeader.path("thumbnail"))
					}

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
								ItemType.Video, ItemType.Song -> preContents.add(
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

					if (preContents.isNotEmpty()) others.add(
						ArtistListContainer(
							ArtistListTopic(
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

	if (title.isEmpty() || subscribers.isEmpty()) return null

	return Artist(
		title = title,
		description = description,
		subscribers = subscribers,
		thumbnail = thumbnail,
		menu = menu,
		others = others
	)
}