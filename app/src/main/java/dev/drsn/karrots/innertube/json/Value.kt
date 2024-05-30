package dev.drsn.karrots.innertube.json

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

val JsonElement?.maybeStringVal get(): String? = this?.jsonPrimitive?.content
val JsonElement?.maybeIntVal get(): Int? = this?.jsonPrimitive?.intOrNull
val JsonElement?.maybeLongVal get(): Long? = this?.jsonPrimitive?.longOrNull
val JsonElement?.maybeFloatVal get(): Float? = this?.jsonPrimitive?.floatOrNull
val JsonElement?.maybeBoolVal get(): Boolean? = this?.jsonPrimitive?.booleanOrNull