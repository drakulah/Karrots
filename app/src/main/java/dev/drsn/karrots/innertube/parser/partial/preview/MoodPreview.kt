package dev.drsn.karrots.innertube.parser.partial.preview

import dev.drsn.karrots.innertube.json.maybeLongVal
import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import dev.drsn.karrots.innertube.utils.nullifyIfEmpty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MoodPreview(
	val title: String,
	val color: Long,
	val browseId: String,
	val params: String
) : PreviewParser.ContentPreview()

fun PreviewParser.parseMoodPreview(obj: JsonElement?): MoodPreview? {
	return MoodPreview(
		color = obj.path("solid.leftStripeColor").maybeLongVal ?: return null,
		title = obj.path("buttonText.runs[0].text").maybeStringVal?.nullifyIfEmpty() ?: return null,
		params = obj.path("clickCommand.browseEndpoint.params").maybeStringVal?.nullifyIfEmpty()
			?: return null,
		browseId = obj.path("clickCommand.browseEndpoint.browseId").maybeStringVal?.nullifyIfEmpty()
			?: return null,
	)
}