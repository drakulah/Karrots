package dev.drsn.karrots.extension

private val SizeFilterRegex = Regex("^(w|h|s)\\d+(-w\\d+|-h\\d+)?")

fun String.setSizeParam(width: Int? = null, height: Int? = null): String {
	val split = this.split('=')
	val lastPart = split.lastOrNull() ?: return this
	var modifiedUrl = this.replace(lastPart, "")

	if (SizeFilterRegex.containsMatchIn(lastPart)) {
		modifiedUrl += if (width != null && height != null) {
			"w$width-h$height-l90-rj"
		} else if (width != null) {
			"w$width-l90-rj"
		} else if (height != null) {
			"h$height-l90-rj"
		} else {
			""
		}
		return modifiedUrl
	}
	return this
}


fun String.setSizeParam(size: Int): String {
	return this.setSizeParam(size, size)
}