package dev.drsn.karrots.innertube.parser.partial.chunk

import dev.drsn.karrots.innertube.json.maybeStringVal
import dev.drsn.karrots.innertube.json.path
import dev.drsn.karrots.innertube.utils.eatFiveStarDoNothing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
enum class Badge {
	ExplicitContent
}

/**
 * Provide __Object.badges__ or __Object.subtitleBadges__
 */
fun ChunkParser.parseBadges(obj: JsonElement?): List<Badge> {
	val badges = arrayListOf<Badge>()

	obj?.jsonArray?.forEach {
		val iconType =
			it.path("musicInlineBadgeRenderer.icon.iconType")?.maybeStringVal?.trim()?.uppercase() ?: ""

		when {
			iconType.contains("EXPLICIT") -> badges.add(Badge.ExplicitContent)
			else -> eatFiveStarDoNothing()
		}
	}

	return badges
}