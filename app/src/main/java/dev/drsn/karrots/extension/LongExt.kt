package dev.drsn.karrots.extension

fun Long.toTimeString(): String {
	val seconds = this / 1000L
	val hrs = seconds / 3600L
	val mins = (seconds % 3600L) / 60L
	val secs = seconds % 60L

	return when {
		hrs > 0 -> String.format("%02d:%02d:%02d", hrs, mins, secs)
		mins > 0 -> String.format("%d:%02d", mins, secs)
		else -> String.format("0:%02d", secs)
	}
}