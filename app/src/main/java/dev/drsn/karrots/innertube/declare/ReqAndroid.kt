package dev.drsn.karrots.innertube.declare

import kotlinx.serialization.Serializable

@Serializable
data class AndroidReqClient(
	val gl: String,
	val hl: String,
	val clientName: String,
	val clientVersion: String,
	val androidSkdVersion: Int,
	val visitorData: String,
)

@Serializable
data class AndroidReqContext(
	val client: AndroidReqClient
)

@Serializable
data class AndroidReqBody(
	val context: AndroidReqContext
)

@Serializable
data class AndroidReqBodyWithBrowse(
	val browseId: String,
	val context: AndroidReqContext
)

@Serializable
data class AndroidReqBodyPlayer(
	val videoId: String,
	val context: AndroidReqContext
)