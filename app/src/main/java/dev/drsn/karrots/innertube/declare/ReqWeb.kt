package dev.drsn.karrots.innertube.declare

import kotlinx.serialization.Serializable

@Serializable
data class WebReqClient(
	val gl: String,
	val hl: String,
	val clientName: String,
	val clientVersion: String,
	val visitorData: String,
)

@Serializable
data class WebReqContext(
	val client: WebReqClient
)

@Serializable
data class WebReqBody(
	val context: WebReqContext
)

@Serializable
data class WebReqBodyWithBrowse(
	val browseId: String,
	val context: WebReqContext
)

@Serializable
data class WebReqBodyWithVidListId(
	val videoId: String,
	val playlistId: String,
	val context: WebReqContext
)

@Serializable
data class WebReqBodyWithQuery(
	val query: String,
	val context: WebReqContext
)

@Serializable
data class WebReqBodyWithQueryAndParams(
	val query: String,
	val params: String,
	val context: WebReqContext
)