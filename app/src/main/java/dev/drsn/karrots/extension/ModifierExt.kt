package dev.drsn.karrots.extension

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min

enum class SizeMode {
	Lowest,
	Highest
}

@Composable
fun Modifier.stickHeight(sizeMode: SizeMode = SizeMode.Highest): Modifier {

	val density = LocalDensity.current
	var currentHeight by remember { mutableStateOf<Dp?>(null) }

	val nm = Modifier
		.onGloballyPositioned {
			val newHeight = with(density) { it.size.height.toDp() }

			if (currentHeight == null) {
				currentHeight = newHeight
				return@onGloballyPositioned
			}

			currentHeight = when (sizeMode) {
				SizeMode.Lowest -> min(currentHeight!!, newHeight)
				SizeMode.Highest -> max(currentHeight!!, newHeight)
			}
		}
		.onSizeChanged {
			val newHeight = with(density) { it.height.toDp() }

			if (currentHeight == null) {
				currentHeight = newHeight
				return@onSizeChanged
			}

			currentHeight = when (sizeMode) {
				SizeMode.Lowest -> min(currentHeight!!, newHeight)
				SizeMode.Highest -> max(currentHeight!!, newHeight)
			}
		}
		.heightIn(
			min = (if (sizeMode == SizeMode.Highest) currentHeight else null) ?: Dp.Unspecified,
			max = (if (sizeMode == SizeMode.Lowest) currentHeight else null) ?: Dp.Unspecified
		)

	return this.then(nm)
}