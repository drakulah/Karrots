package dev.drsn.karrots.innertube.utils

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

fun mixedJsonArray(vararg e: JsonElement?): List<JsonElement> {
	val list = arrayListOf<JsonElement>()
	e.forEach { f -> f?.jsonArray?.forEach(list::add) }
	return list
}