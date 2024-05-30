package dev.drsn.karrots.innertube.parser

import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import dev.drsn.karrots.innertube.parser.partial.chunk.ChunkParser
import dev.drsn.karrots.innertube.parser.partial.chunk.ItemType
import dev.drsn.karrots.innertube.parser.partial.chunk.ThumbnailInfo
import dev.drsn.karrots.innertube.parser.partial.chunk.parseContinuation
import dev.drsn.karrots.innertube.parser.partial.chunk.parseId
import dev.drsn.karrots.innertube.parser.partial.chunk.parseItemType
import dev.drsn.karrots.innertube.parser.partial.chunk.parseThumbnail
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.parser.partial.preview.parseAlbumPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseArtistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseMoodPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parsePlaylistPreview
import dev.drsn.karrots.innertube.parser.partial.preview.parseTrackPreview
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@Serializable
data class ExploreListTopic(
	val title: String,
	val subtitle: String?,
	val browseId: String?,
	val thumbnail: List<ThumbnailInfo>
)

@Serializable
data class ExploreListContainer(
	val topic: ExploreListTopic,
	val preContents: List<PreviewParser.ContentPreview>
)

@Serializable
data class Explore(
	val continuation: String?,
	val contents: List<ExploreListContainer>
)

fun ResponseParser.parseExplore(obj: JsonElement?): Explore? {

	var continuation: String? = null
	val contents = arrayListOf<ExploreListContainer>()

	(obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer")
		?: obj.path("continuationContents.sectionListContinuation"))?.let { sharedSection ->

		continuation = ChunkParser.parseContinuation(sharedSection.path("continuations[0]"))

		sharedSection.path("contents")
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

						sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer")
							.let { sharedHeader ->
								topicTitle =
									sharedHeader.path("title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
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

									else -> preContents.add(
										PreviewParser.parseMoodPreview(eachItem.path("musicNavigationButtonRenderer"))
											?: return@forEach
									)
								}

							}

						if (preContents.isNotEmpty()) contents.add(
							ExploreListContainer(
								ExploreListTopic(
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
	}

	return Explore(
		continuation = continuation,
		contents = contents
	)
}