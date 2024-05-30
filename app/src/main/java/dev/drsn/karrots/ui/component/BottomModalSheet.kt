package dev.drsn.karrots.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.theme.LocalTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BottomModalSheet(
	state: ModalBottomSheetState
) {

	val theme = LocalTheme.current

	if (state.progress != 0f) {
		AnimatedVisibility(
			visible = !state.isDismissed,
			enter = fadeIn(),
			exit = fadeOut()
		) {
			BackHandler(onBack = state::dismiss)

			Box(
				modifier = Modifier
					.fillMaxSize(),
				contentAlignment = Alignment.BottomCenter
			) {

				Layer(
					modifier = Modifier
						.fillMaxSize()
						.background(Color.Black.copy(0.5f))
						.clickable(
							enabled = true,
							onClick = state::dismiss,
							interactionSource = remember { MutableInteractionSource() },
							indication = null
						)
				) {}

				Layer(
					modifier = Modifier
						.offset {
							val y = (state.expandedBound - state.value)
								.roundToPx()
								.coerceAtLeast(0)
							IntOffset(x = 0, y = y)
						}
						.fillMaxWidth(),
					contentAlignment = Alignment.BottomCenter
				) {

					Column(
						modifier = Modifier
							.padding(horizontal = 8.dp)
							.padding(bottom = 4.dp)
							.systemBarsPadding()
							.fillMaxWidth()
							.shadow(
								elevation = 2.dp,
								shape = RoundedCornerShape(24.dp),
							)
							.background(
								color = theme.colorScheme.surfaceContainerLowest,
								shape = RoundedCornerShape(24.dp)
							)
					) {

						Box(
							modifier = Modifier
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
											state.performFling(velocity, state::dismiss)
										}
									)
								}
								.fillMaxWidth()
								.padding(vertical = 16.dp),
							contentAlignment = Alignment.Center
						) {

							Box(
								modifier = Modifier
									.width(48.dp)
									.height(4.dp)
									.background(
										color = theme.colorScheme.onSurfaceVariant,
										shape = RoundedCornerShape(12.dp)
									)
							)
						}

						Box(
							modifier = Modifier,
							content = state.content.value
						)

					}

				}
			}
		}
	}

}

@Stable
class ModalBottomSheetState(
	draggableState: DraggableState,
	private val coroutineScope: CoroutineScope,
	private val animatable: Animatable<Dp, AnimationVector1D>,
	private val onAnchorChanged: (Int) -> Unit,
	val collapsedBound: Dp
) : DraggableState by draggableState {
	private val dismissedBound: Dp
		get() = animatable.lowerBound!!

	val expandedBound: Dp
		get() = animatable.upperBound!!

	var dragEnabled by mutableStateOf(true)

	val value by animatable.asState()

	var content: MutableState<@Composable (BoxScope.() -> Unit)> = mutableStateOf({})
		private set

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

	fun setContent(content: @Composable (BoxScope.() -> Unit)): ModalBottomSheetState {
		this.content.value = content
		return this
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

@Composable
fun rememberModalBottomSheetState(
	dismissedBound: Dp,
	expandedBound: Dp,
	initialAnchor: Int = dismissedAnchor
): ModalBottomSheetState {
	val density = LocalDensity.current
	val coroutineScope = rememberCoroutineScope()

	var previousAnchor by rememberSaveable {
		mutableIntStateOf(initialAnchor)
	}

	return remember(dismissedBound, expandedBound, dismissedBound, coroutineScope) {
		val initialValue = when (previousAnchor) {
			expandedAnchor -> expandedBound
			collapsedAnchor -> dismissedBound
			dismissedAnchor -> dismissedBound
			else -> error("Unknown BottomSheet anchor")
		}

		val animatable = Animatable(initialValue, Dp.VectorConverter).also {
			it.updateBounds(dismissedBound.coerceAtMost(expandedBound), expandedBound)
		}

		ModalBottomSheetState(
			draggableState = DraggableState { delta ->
				coroutineScope.launch {
					animatable.snapTo(animatable.value - with(density) { delta.toDp() })
				}
			},
			onAnchorChanged = { previousAnchor = it },
			coroutineScope = coroutineScope,
			animatable = animatable,
			collapsedBound = dismissedBound
		)
	}
}