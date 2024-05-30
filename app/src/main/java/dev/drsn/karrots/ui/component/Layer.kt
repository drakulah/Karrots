package dev.drsn.karrots.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics

@Composable
fun Layer(
	modifier: Modifier = Modifier,
	contentAlignment: Alignment = Alignment.TopStart,
	enableInteractionPassThrough: Boolean = false,
	content: @Composable () -> Unit
) {

	val mod = if (!enableInteractionPassThrough) modifier
		.semantics(mergeDescendants = false) {}
		.pointerInput(Unit) {}
	else modifier

	Box(
		modifier = mod,
//		propagateMinConstraints = true,
		contentAlignment = contentAlignment
	) {
		content()
	}
}