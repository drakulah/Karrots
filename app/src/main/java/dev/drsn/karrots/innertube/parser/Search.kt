package dev.drsn.karrots.innertube.parser

import dev.drsn.karrots.innertube.json.maybeBoolVal
import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import dev.drsn.karrots.innertube.parser.partial.chunk.ChunkParser
import dev.drsn.karrots.innertube.parser.partial.chunk.ItemType
import dev.drsn.karrots.innertube.parser.partial.chunk.ThumbnailInfo
import dev.drsn.karrots.innertube.parser.partial.chunk.parseContinuation
import dev.drsn.karrots.innertube.parser.partial.chunk.parseItemType
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
data class SearchParamChip(
	val title: String,
	val isCurrent: Boolean,
	val continuation: String
)

@Serializable
data class SearchListTopic(
	val title: String,
	val subtitle: String?,
	val browseId: String?,
	val thumbnail: List<ThumbnailInfo>
)

@Serializable
data class SearchListContainer(
	val topic: SearchListTopic,
	val preContents: List<PreviewParser.ContentPreview>
)

@Serializable
data class SearchTopResult(
	val title: String,
	val topResult: PreviewParser.ContentPreview,
	val otherPriorResults: List<PreviewParser.ContentPreview>
)

@Serializable
data class Search(
	val topResult: SearchTopResult?,
	val chips: List<SearchParamChip>,
	val contents: List<SearchListContainer>,
	val continuation: String?
)

fun ResponseParser.parseSearch(obj: JsonElement?): Search {

	var continuation: String? = null
	var topResult: SearchTopResult? = null
	val chips = arrayListOf<SearchParamChip>()
	val contents = arrayListOf<SearchListContainer>()

	obj.path("contents.tabbedSearchResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer")
		?.jsonObject
		?.let { sharedSection ->

			sharedSection.path("header.chipCloudRenderer.chips")
				?.jsonArray
				?.forEach { eachChip ->

					eachChip.path("chipCloudChipRenderer")
						?.jsonObject
						?.let { sharedChip ->
							chips.add(
								SearchParamChip(
									isCurrent = sharedChip.path("isSelected").maybeBoolVal ?: false,
									title = sharedChip.path("text.runs[0].text").maybeStringVal?.nullifyIfEmpty()
										?: return@let,
									continuation = sharedChip.path("navigationEndpoint.searchEndpoint.params").maybeStringVal?.nullifyIfEmpty()
										?: return@let,
								)
							)
						}
				}

			sharedSection.path("contents")
				?.jsonArray
				?.forEach { eachComponent ->

					/** Parse Top Result **/
					eachComponent.path("musicCardShelfRenderer")
						?.jsonObject
						?.let { sharedContainer ->

							val topResultMainResult: PreviewParser.ContentPreview =
								when (ChunkParser.parseItemType(sharedContainer.path("title.runs[0].navigationEndpoint"))) {
									ItemType.Video, ItemType.Song, ItemType.Podcast -> PreviewParser.parseTrackPreview(
										sharedContainer
									)

									ItemType.AlbumPreview -> PreviewParser.parseAlbumPreview(sharedContainer)
									ItemType.ArtistPreview -> PreviewParser.parseArtistPreview(sharedContainer)
									ItemType.PlaylistPreview -> PreviewParser.parsePlaylistPreview(sharedContainer)
									else -> null
								} ?: return@let

							val otherPriorResults: ArrayList<PreviewParser.ContentPreview> = arrayListOf()

							sharedContainer.path("contents")
								?.jsonArray
								?.forEach { eachItem ->

									val itemRenderer =
										eachItem.path("musicTwoRowItemRenderer")
											?: eachItem.path("musicResponsiveListItemRenderer")
									val itemType = ChunkParser.parseItemType(
										eachItem.path("musicTwoRowItemRenderer.navigationEndpoint")
											?: eachItem.path("musicResponsiveListItemRenderer.navigationEndpoint")
											?: eachItem.path("musicResponsiveListItemRenderer.flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].navigationEndpoint")
									)

									when (itemType) {
										ItemType.Video, ItemType.Song, ItemType.Podcast -> otherPriorResults.add(
											PreviewParser.parseTrackPreview(itemRenderer) ?: return@forEach
										)

										ItemType.AlbumPreview -> otherPriorResults.add(
											PreviewParser.parseAlbumPreview(itemRenderer) ?: return@forEach
										)

										ItemType.ArtistPreview -> otherPriorResults.add(
											PreviewParser.parseArtistPreview(itemRenderer) ?: return@forEach
										)

										ItemType.PlaylistPreview -> otherPriorResults.add(
											PreviewParser.parsePlaylistPreview(itemRenderer) ?: return@forEach
										)

										else -> eatFiveStarDoNothing()
									}
								}

							topResult = SearchTopResult(
								title = sharedContainer.path("header.musicCardShelfHeaderBasicRenderer.title.runs[0].text").maybeStringVal.nullifyIfEmpty()
									?: "Top result",
								topResult = topResultMainResult,
								otherPriorResults = otherPriorResults
							)
						}

					/** Parse Search Result **/
					eachComponent.path("musicShelfRenderer")
						?.jsonObject
						?.let { sharedContainer ->

							continuation = ChunkParser.parseContinuation(sharedContainer.path("continuations[0]"))

							val preContents: ArrayList<PreviewParser.ContentPreview> = arrayListOf()

							sharedContainer.path("contents")
								?.jsonArray
								?.forEach { eachItem ->

									val itemRenderer =
										eachItem.path("musicTwoRowItemRenderer")
											?: eachItem.path("musicResponsiveListItemRenderer")
									val itemType = ChunkParser.parseItemType(
										eachItem.path("musicTwoRowItemRenderer.navigationEndpoint")
											?: eachItem.path("musicResponsiveListItemRenderer.navigationEndpoint")
											?: eachItem.path("musicResponsiveListItemRenderer.flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].navigationEndpoint")
									)

									when (itemType) {
										ItemType.Video, ItemType.Song, ItemType.Podcast -> preContents.add(
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

							contents.add(
								SearchListContainer(
									topic = SearchListTopic(
										title = sharedContainer.path("title.runs[0].text").maybeStringVal.nullifyIfEmpty()
											?: "",
										browseId = null,
										subtitle = null,
										thumbnail = listOf()
									),
									preContents = preContents
								)
							)
						}
				}
		}

	obj.path("continuationContents.musicShelfContinuation")
		?.jsonObject
		?.let { sharedContainer ->

			continuation = ChunkParser.parseContinuation(sharedContainer.path("continuations[0]"))

			val preContents: ArrayList<PreviewParser.ContentPreview> = arrayListOf()

			sharedContainer.path("contents")
				?.jsonArray
				?.forEach { eachItem ->

					val itemRenderer =
						eachItem.path("musicTwoRowItemRenderer")
							?: eachItem.path("musicResponsiveListItemRenderer")
					val itemType = ChunkParser.parseItemType(
						eachItem.path("musicTwoRowItemRenderer.navigationEndpoint")
							?: eachItem.path("musicResponsiveListItemRenderer.navigationEndpoint")
							?: eachItem.path("musicResponsiveListItemRenderer.flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].navigationEndpoint")
					)

					when (itemType) {
						ItemType.Video, ItemType.Song, ItemType.Podcast -> preContents.add(
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

			contents.add(
				SearchListContainer(
					topic = SearchListTopic(
						title = sharedContainer.path("title.runs[0].text").maybeStringVal.nullifyIfEmpty()
							?: "",
						browseId = null,
						subtitle = null,
						thumbnail = listOf()
					),
					preContents = preContents
				)
			)
		}

	return Search(
		chips = chips,
		topResult = topResult,
		contents = contents,
		continuation = continuation
	)
}