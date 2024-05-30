package dev.drsn.karrots.innertube.utils

fun List<String>.joinToString(joiner: String = " "): String {
	var s = ""
	forEach {
		if (it.isEmpty()) return@forEach
		if (s.isNotEmpty()) s += joiner
		s += it
	}
	return s
}

fun List<String>.autoJoinToString(): String {
	val items = arrayListOf<String>()
	forEach {
		val e = it.trim()
		if (e.isNotEmpty()) items.add(e)
	}
	var s = ""
	for (i in 0 until items.size) {
		if (s.isNotEmpty() && i == items.size - 1) s += " & "
		else if (s.isNotEmpty()) s += ", "
		s += items[i]
	}
	return s
}

fun <T> List<T>.joinToString(joiner: String = " ", inlineFn: (T) -> String): String {
	var s = ""
	forEach {
		val transformedString = inlineFn(it)
		if (transformedString.isEmpty()) return@forEach
		if (s.isNotEmpty()) s += joiner
		s += transformedString
	}
	return s
}

fun <T> List<T>.autoJoinToString(inlineFn: (T) -> String): String {
	val items = arrayListOf<String>()
	forEach {
		val e = inlineFn(it).trim()
		if (e.isNotEmpty()) items.add(e)
	}
	var s = ""
	for (i in 0 until items.size) {
		if (s.isNotEmpty() && i == items.size - 1) s += " & "
		else if (s.isNotEmpty()) s += ", "
		s += items[i]
	}
	return s
}

fun <T> ArrayList<T>.removeWhen(filter: (T) -> Boolean): Boolean {
	for (i in 0 until size) {
		if (filter(this[i])) {
			removeAt(i)
			return true
		}
	}
	return false
}

fun <T> ArrayList<T>.findIndex(filter: (T) -> Boolean): Int {
	for (i in 0 until size) {
		if (filter(this[i])) return i
	}
	return -1
}

fun <T> MutableList<T>.removeWhen(filter: (T) -> Boolean): Boolean {
	for (i in 0 until size) {
		if (filter(this[i])) {
			removeAt(i)
			return true
		}
	}
	return false
}

fun <T> MutableList<T>.findIndex(filter: (T) -> Boolean): Int {
	for (i in 0 until size) {
		if (filter(this[i])) return i
	}
	return -1
}