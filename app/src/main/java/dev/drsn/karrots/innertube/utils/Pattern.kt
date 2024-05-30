package dev.drsn.karrots.innertube.utils

private object Pattern {
	val yearText = Regex("^\\d{4}\$")
	val separatorText = Regex("^([&,•\\-_|])\$")
	val durationText = Regex("^((\\d+:)+)?\\d+\$")
	val albumType = Regex("^(EP|Album|Single)\$")
	val trackCount = Regex("^[0-9]+\\s(song|track)s?\$")
	val trackPlays = Regex("^(\\d+(?:\\.\\d+)?)[B-Mb-m]?((\\sviews?)?|(\\splays?)?)\$")
	val likeCount = Regex("^(\\d+(?:\\.\\d+)?)[B-Mb-m]?(\\slikes?)?\$")
	val mimeTypeOpus = Regex("^audio/(webm|mp4); codecs=\"opus\"\$")
	val subscriberCount = Regex("^(\\d+(?:\\.\\d+)?)[B-Mb-m]?(\\ssubscribers?)?\$")
	val playlistDuration = Regex("^((\\d+(?:\\.\\d+)?)\\+?\\s(minute|hour|day)s?(,\\s)?)+\$")
	val dateText = Regex("^\\w+\\s\\d\\d?,\\s\\d{4}\$")
	val itemType = listOf(
		"EP",
		"Episode",
		"Profile",
		"Podcast",
		"Album",
		"Single",
		"Artist",
		"Video",
		"Song",
		"Playlist"
	)
}

fun String?.isYearText(): Boolean = this?.let { Pattern.yearText.matches(it) } ?: false
fun String?.isSeparatorText(): Boolean = this?.let { Pattern.separatorText.matches(it) } ?: false
fun String?.isDurationText(): Boolean = this?.let { Pattern.durationText.matches(it) } ?: false
fun String?.isAlbumType(): Boolean = this?.let { Pattern.albumType.matches(it) } ?: false
fun String?.isTrackCount(): Boolean = this?.let { Pattern.trackCount.matches(it) } ?: false
fun String?.isTrackPlays(): Boolean = this?.let { Pattern.trackPlays.matches(it) } ?: false
fun String?.isLikeCount(): Boolean = this?.let { Pattern.likeCount.matches(it) } ?: false
fun String?.isMimeTypeOpus(): Boolean = this?.let { Pattern.mimeTypeOpus.matches(it) } ?: false
fun String?.isSubscriberCount(): Boolean =
	this?.let { Pattern.subscriberCount.matches(it) } ?: false

fun String?.isPlaylistDuration(): Boolean =
	this?.let { Pattern.playlistDuration.matches(it) } ?: false

fun String?.isItemType(): Boolean = this?.let { it in Pattern.itemType } ?: false
fun String?.isDateText(): Boolean = this?.let { Pattern.dateText.matches(it) } ?: false

fun String?.isMaybeTitle(): Boolean = this?.let {
	!it.isYearText()
			&& !it.isSeparatorText()
			&& !it.isDurationText()
			&& !it.isAlbumType()
			&& !it.isTrackCount()
			&& !it.isTrackPlays()
			&& !it.isLikeCount()
			&& !it.isMimeTypeOpus()
			&& !it.isSubscriberCount()
			&& !it.isPlaylistDuration()
			&& !it.isItemType()
			&& !it.isDateText()
} ?: false