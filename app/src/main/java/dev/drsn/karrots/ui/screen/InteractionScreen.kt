package dev.drsn.karrots.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.navigation.NavController
import dev.drsn.karrots.LocalPageRouter
import dev.drsn.karrots.LocalPlayer
import dev.drsn.karrots.audio.event.EventListener
import dev.drsn.karrots.audio.types.PlaybackState
import dev.drsn.karrots.ui.component.BottomSheet
import dev.drsn.karrots.ui.component.Layer
import dev.drsn.karrots.ui.component.collapsedAnchor
import dev.drsn.karrots.ui.component.rememberBottomSheetState
import dev.drsn.karrots.ui.player.FullPagePlayer
import dev.drsn.karrots.ui.player.MiniPlayer
import dev.drsn.karrots.ui.router.PageRouter
import dev.drsn.karrots.ui.theme.LocalTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractionScreen() {

	val theme = LocalTheme.current
	val player = LocalPlayer.current

	val pageRouter = LocalPageRouter.current
	var currentRoute by remember { mutableStateOf(pageRouter.currentBackStackEntry?.destination?.route) }

	val config = LocalConfiguration.current
	val systemBar = WindowInsets.systemBars.asPaddingValues()
	val topPadding = systemBar.calculateTopPadding()
	val bottomPadding = systemBar.calculateBottomPadding()
	val bottomNavHeight = 80.dp + bottomPadding
	val expandedBound = config.screenHeightDp.dp + topPadding + bottomPadding

	val bottomSheetState = rememberBottomSheetState(
		dismissedBound = 0.dp,
		collapsedBound = 60.dp,
		expandedBound = expandedBound,
		initialAnchor = collapsedAnchor // expandedAnchor
	)

	val miniPlayerColor by animateColorAsState(
		targetValue = if (bottomSheetState.progress > 0f) theme.colorScheme.surfaceContainer
		else theme.colorScheme.surfaceContainerHigh,
		label = "Mini Player Color"
	)

	DisposableEffect(Unit) {
		val eventListener = object : EventListener {
			override fun onPlaybackStateChange(state: PlaybackState) {
				if (bottomSheetState.isDismissed && state != PlaybackState.Paused) bottomSheetState.collapseSoft()
			}

			override fun onMediaItemChange() {
				if (bottomSheetState.isDismissed) bottomSheetState.collapseSoft()
			}
		}

		player.addEventListener(eventListener)
		onDispose { player.removeEventListener(eventListener) }
	}

	DisposableEffect(Unit) {
		val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
			currentRoute = destination.route
		}

		pageRouter.addOnDestinationChangedListener(listener)
		onDispose { pageRouter.removeOnDestinationChangedListener(listener) }
	}

	Scaffold(
		modifier = Modifier,
		contentWindowInsets = WindowInsets(0.dp)
	) { contentPadding ->

		Box(
			modifier = Modifier
				.padding(contentPadding),
			contentAlignment = Alignment.BottomCenter
		) {

			Layer(
				modifier = Modifier.fillMaxSize()
			) {
				PageRouter()
			}

			BottomSheet(
				modifier = Modifier
					.padding(
						bottom = lerp(
							0.dp,
							bottomNavHeight,
							1f - bottomSheetState.progress.coerceIn(0f..1f)
						)
					)
					.background(theme.colorScheme.surfaceContainer),
				state = bottomSheetState,
				onDismiss = {
					player.pause()
					bottomSheetState.dismiss()
				}
			) {

				/**
				 * Mini Player
				 */

				/**
				 * Mini Player
				 */

				if (bottomSheetState.progress < 1f) {
					MiniPlayer(
						modifier = Modifier
							.background(miniPlayerColor)
							.graphicsLayer {
								compositingStrategy = CompositingStrategy.Offscreen
								alpha = (1f - bottomSheetState.progress * 5f).coerceIn(0f..1f)
							}
					)
				}

				/**
				 * Main Player
				 */

				/**
				 * Main Player
				 */

				if (bottomSheetState.progress > 0f) {
					FullPagePlayer(
						bottomSheetState = bottomSheetState,
						modifier = Modifier
							.graphicsLayer {
								compositingStrategy = CompositingStrategy.Offscreen
								alpha =
									((bottomSheetState.progress - 0.2f).coerceIn(0f..0.8f) * 2f).coerceIn(0f..1f)
							}
					)
				}

			}

			BottomNavigation(
				modifier = Modifier
					.offset {
						IntOffset(
							x = 0,
							y = (bottomNavHeight.value * bottomSheetState.progress.coerceAtLeast(0f)).dp.roundToPx()
						)
					}
					.fillMaxWidth()
					.height(bottomNavHeight)
					.background(theme.colorScheme.surfaceContainer)
					.padding(bottom = bottomPadding))
		}
	}
}