package dev.drsn.karrots.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BottomSheet(
	state: BottomSheetState,
	modifier: Modifier = Modifier,
	onDismiss: (() -> Unit)? = null,
	content: @Composable BoxScope.() -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxSize(),
		contentAlignment = Alignment.BottomCenter
	) {
		Layer(
			modifier = Modifier
				.offset {
					val y = if (state.progress < 0) (state.collapsedBound - state.value)
						.roundToPx()
						.coerceAtLeast(0)
					else 0
					IntOffset(x = 0, y = y)
				}
				.then(modifier)
				.fillMaxWidth()
				.height(state.value.coerceAtLeast(state.collapsedBound))
				.clip(RectangleShape)
				.pointerInput(state) {
					val velocityTracker = VelocityTracker()

					detectVerticalDragGestures(
						onVerticalDrag = { change, dragAmount ->
							if (!state.dragEnabled) return@detectVerticalDragGestures
							velocityTracker.addPointerInputChange(change)
							state.dispatchRawDelta(dragAmount)
						},
						onDragCancel = {
							velocityTracker.resetTracking()
							state.snapTo(state.collapsedBound)
						},
						onDragEnd = {
							val velocity = -velocityTracker.calculateVelocity().y
							velocityTracker.resetTracking()
							state.performFling(velocity, onDismiss)
						}
					)
				}
		) {

			Box(
				modifier = Modifier
					.fillMaxSize(),
				content = content
			)

			if (!state.isCollapsed && !state.isDismissed) {
				BackHandler(onBack = state::collapseSoft)
			}

		}
	}
}

@Stable
class BottomSheetState(
	draggableState: DraggableState,
	private val coroutineScope: CoroutineScope,
	private val animatable: Animatable<Dp, AnimationVector1D>,
	private val onAnchorChanged: (Int) -> Unit,
	val collapsedBound: Dp,
) : DraggableState by draggableState {
	private val dismissedBound: Dp
		get() = animatable.lowerBound!!

	val expandedBound: Dp
		get() = animatable.upperBound!!

	var dragEnabled by mutableStateOf(true)

	val value by animatable.asState()

	val isDismissed by derivedStateOf {
		value == animatable.lowerBound!!
	}

	val isCollapsed by derivedStateOf {
		value == collapsedBound
	}

	val isExpanded by derivedStateOf {
		value == animatable.upperBound
	}

	val progress by derivedStateOf {
		1f - (animatable.upperBound!! - animatable.value) / (animatable.upperBound!! - collapsedBound)
	}

	private fun collapse(animationSpec: AnimationSpec<Dp>) {
		onAnchorChanged(collapsedAnchor)
		coroutineScope.launch {
			animatable.animateTo(collapsedBound, animationSpec)
		}
	}

	private fun expand(animationSpec: AnimationSpec<Dp>) {
		onAnchorChanged(expandedAnchor)
		coroutineScope.launch {
			animatable.animateTo(animatable.upperBound!!, animationSpec)
		}
	}

	private fun collapse() {
		collapse(SpringSpec())
	}

	private fun expand() {
		expand(SpringSpec())
	}

	fun collapseSoft() {
		collapse(tween(300))
	}

	fun expandSoft() {
		expand(tween(300))
	}

	fun dismiss() {
		onAnchorChanged(dismissedAnchor)
		coroutineScope.launch {
			animatable.animateTo(animatable.lowerBound!!)
		}
	}

	fun snapTo(value: Dp) {
		coroutineScope.launch {
			animatable.snapTo(value)
		}
	}

	fun performFling(velocity: Float, onDismiss: (() -> Unit)?) {
		if (velocity > 250) {
			expand()
		} else if (velocity < -250) {
			if (value < collapsedBound && onDismiss != null) {
				dismiss()
				onDismiss.invoke()
			} else {
				collapse()
			}
		} else {
			val l0 = dismissedBound
			val l1 = (collapsedBound - dismissedBound) / 2
			val l2 = (expandedBound - collapsedBound) / 2
			val l3 = expandedBound

			when (value) {
				in l0..l1 -> {
					if (onDismiss != null) {
						dismiss()
						onDismiss.invoke()
					} else {
						collapse()
					}
				}

				in l1..l2 -> collapse()
				in l2..l3 -> expand()
				else -> Unit
			}
		}
	}

	val preUpPostDownNestedScrollConnection
		get() = object : NestedScrollConnection {
			var isTopReached = false

			override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
				if (isExpanded && available.y < 0) {
					isTopReached = false
				}

				return if (isTopReached && available.y < 0 && source == NestedScrollSource.Drag) {
					dispatchRawDelta(available.y)
					available
				} else {
					Offset.Zero
				}
			}

			override fun onPostScroll(
				consumed: Offset,
				available: Offset,
				source: NestedScrollSource
			): Offset {
				if (!isTopReached) {
					isTopReached = consumed.y == 0f && available.y > 0
				}

				return if (isTopReached && source == NestedScrollSource.Drag) {
					dispatchRawDelta(available.y)
					available
				} else {
					Offset.Zero
				}
			}

			override suspend fun onPreFling(available: Velocity): Velocity {
				return if (isTopReached) {
					val velocity = -available.y
					performFling(velocity, null)

					available
				} else {
					Velocity.Zero
				}
			}

			override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
				isTopReached = false
				return Velocity.Zero
			}
		}
}

const val expandedAnchor = 2
const val collapsedAnchor = 1
const val dismissedAnchor = 0

@Composable
fun rememberBottomSheetState(
	dismissedBound: Dp,
	expandedBound: Dp,
	collapsedBound: Dp = dismissedBound,
	initialAnchor: Int = dismissedAnchor
): BottomSheetState {
	val density = LocalDensity.current
	val coroutineScope = rememberCoroutineScope()

	var previousAnchor by rememberSaveable {
		mutableIntStateOf(initialAnchor)
	}

	return remember(dismissedBound, expandedBound, collapsedBound, coroutineScope) {
		val initialValue = when (previousAnchor) {
			expandedAnchor -> expandedBound
			collapsedAnchor -> collapsedBound
			dismissedAnchor -> dismissedBound
			else -> error("Unknown BottomSheet anchor")
		}

		val animatable = Animatable(initialValue, Dp.VectorConverter).also {
			it.updateBounds(dismissedBound.coerceAtMost(expandedBound), expandedBound)
		}

		BottomSheetState(
			draggableState = DraggableState { delta ->
				coroutineScope.launch {
					animatable.snapTo(animatable.value - with(density) { delta.toDp() })
				}
			},
			onAnchorChanged = { previousAnchor = it },
			coroutineScope = coroutineScope,
			animatable = animatable,
			collapsedBound = collapsedBound
		)
	}
}