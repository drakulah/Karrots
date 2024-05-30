package dev.drsn.karrots.util

object Random {
	fun generateCodeIncludingSymbols(size: Int): String {
		var s = size
		val letters = arrayListOf(
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '!',
			'@', '#', '$', '%', '&', '-', '=', '+', '_',
			'?', '.'
		)
		val randomCode = StringBuilder()
		while (s-- > 0) randomCode.append(letters[(0..73).random()])
		return randomCode.toString()
	}

//	fun generateCodeIncludingSymbols(size: Int, notEqualTo: String): String {
//		val code = generateCode(size)
//		return if (code == notEqualTo)
//			generateCodeIncludingSymbols(size, notEqualTo)
//		else
//			code
//	}
//
//	fun generateCodeIncludingSymbols(notEqualTo: String): String {
//		val code = generateCode(notEqualTo.length)
//		return if (code == notEqualTo)
//			generateCodeIncludingSymbols(notEqualTo)
//		else
//			code
//	}

	private fun generateCode(size: Int): String {
		var s = size
		val letters = arrayListOf(
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9'
		)
		val randomCode = StringBuilder()
		while (s-- > 0) randomCode.append(letters[(0..61).random()])
		return randomCode.toString()
	}

	fun generateCode(size: Int, notEqualTo: String): String {
		val code = generateCode(size)
		return if (code == notEqualTo)
			generateCode(size, notEqualTo)
		else
			code
	}

//	fun generateCode(notEqualTo: String): String {
//		val code = generateCode(notEqualTo.length)
//		return if (code == notEqualTo)
//			generateCode(notEqualTo)
//		else
//			code
//	}
}