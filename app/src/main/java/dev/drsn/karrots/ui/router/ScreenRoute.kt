package dev.drsn.karrots.ui.router

sealed class ScreenRoute(val path: String) {
	data object Interaction : ScreenRoute(SCREEN_ROUTE_INTERACTION_PATH)
}