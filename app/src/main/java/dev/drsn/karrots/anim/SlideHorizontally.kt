package dev.drsn.karrots.anim

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private const val ANIMATION_DUR = 500

fun slideInToLeft(): EnterTransition {
	return slideInHorizontally(
		initialOffsetX = { it },
		animationSpec = tween(durationMillis = ANIMATION_DUR)
	)
}

fun slideOutToLeft(): ExitTransition {
	return slideOutHorizontally(
		targetOffsetX = { -it },
		animationSpec = tween(durationMillis = ANIMATION_DUR)
	)
}