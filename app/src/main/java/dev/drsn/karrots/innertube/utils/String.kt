package dev.drsn.karrots.innertube.utils

//fun List<String>.removeEmpty(): List<String> {
//	val arr = arrayListOf<String>()
//
//	for (e in this) if (e.trim().isNotEmpty()) arr.add(e)
//
//	return arr
//}

fun List<String>.removeEmptyNTrim(): List<String> {
	val arr = arrayListOf<String>()

	for (e in this) if (e.trim().isNotEmpty()) arr.add(e.trim())

	return arr
}

//fun List<String>.mix(strArr: List<String>): List<String> {
//	(this as ArrayList<String>).addAll(strArr)
//	return this
//}

fun String?.nullifyIfEmpty(): String? = if (this.isNullOrEmpty()) null else this.trim()