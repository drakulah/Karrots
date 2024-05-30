package dev.drsn.karrots.innertube.utils

import java.util.concurrent.CancellationException

internal inline fun <T> runCatchingNonCancellable(block: () -> T): T? {
	val result = runCatching(block)

//	val err = result.exceptionOrNull()
//	if (err != null) throw err

	return when (result.exceptionOrNull()) {
		is CancellationException -> null
		else -> result.getOrNull()
	}
}