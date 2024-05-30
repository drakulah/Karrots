package dev.drsn.karrots.innertube.json

import dev.drsn.karrots.innertube.utils.removeEmptyNTrim
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

fun JsonElement?.path(path: String): JsonElement? {
	var retJsonElem: JsonElement? = this

	path.split('.')
		.removeEmptyNTrim()
		.forEach { key ->

			if (!Regex("\\[\\d+]\$").containsMatchIn(key)) {
				retJsonElem = retJsonElem?.jsonObject?.get(key)
				return@forEach
			}

			key.split('[')
				.removeEmptyNTrim()
				.forEachIndexed { index, param ->

					if (index == 0) {
						retJsonElem = retJsonElem?.jsonObject?.get(param)
						return@forEachIndexed
					}

					val maybeArrIndex = param.removeSuffix("]")

					if (!Regex("\\d+").matches(maybeArrIndex)) {
						retJsonElem = retJsonElem?.jsonObject?.get(maybeArrIndex)
						return@forEachIndexed
					}

					retJsonElem = retJsonElem?.jsonArray?.get(maybeArrIndex.toInt())
				}
		}

	return retJsonElem
}