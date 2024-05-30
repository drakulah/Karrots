package dev.drsn.karrots.util

import java.net.URLDecoder
import java.net.URLEncoder

fun String.encodeComponent(): String {
	return URLEncoder.encode(this, "utf-8")
}

fun String.decodeComponent(): String {
	return URLDecoder.decode(this, "utf-8")
}