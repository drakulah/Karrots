package dev.drsn.karrots.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

//class DynamicColor(hue: Float? = null, darkMode: Boolean? = null) {
//
//	private val defaultHue = 0f
//
//	var accent by mutableStateOf(Color.hsl(defaultHue, 0.8f, 0.7f))
//		private set
//
//	var background by mutableStateOf(Color.hsl(defaultHue, 0.10f, 0.08f))
//		private set
//
//	var primary by mutableStateOf(Color.hsl(defaultHue, 0.02f, 0.85f))
//		private set
//	var secondary by mutableStateOf(Color.hsl(defaultHue, 0.02f, 0.55f))
//		private set
//	var tertiary by mutableStateOf(Color.hsl(defaultHue, 0.02f, 0.35f))
//		private set
//
//	var primaryContainer by mutableStateOf(Color.hsl(defaultHue, 0.10f, 0.10f))
//		private set
//	var secondaryContainer by mutableStateOf(Color.hsl(defaultHue, 0.20f, 0.15f))
//		private set
//	var tertiaryContainer by mutableStateOf(Color.hsl(defaultHue, 0.30f, 0.20f))
//		private set
//
//	init {
//		with(hue, darkMode)
//	}
//
//	fun with(hue: Float? = null, darkMode: Boolean? = null): DynamicColor {
//		val m = darkMode ?: false
//		val h = hue?.coerceIn(0f..360f)
//
//		primary.toHsl()
//			.let { primary = Color.hsl(h ?: it.hue, it.saturation, if (m) 0.85f else 0.15f) }
//		secondary.toHsl()
//			.let { secondary = Color.hsl(h ?: it.hue, it.saturation, if (m) 0.55f else 0.45f) }
//		tertiary.toHsl()
//			.let { tertiary = Color.hsl(h ?: it.hue, it.saturation, if (m) 0.35f else 0.65f) }
//		primaryContainer.toHsl()
//			.let { primaryContainer = Color.hsl(h ?: it.hue, it.saturation, if (m) 0.10f else 0.90f) }
//		secondaryContainer.toHsl()
//			.let { secondaryContainer = Color.hsl(h ?: it.hue, it.saturation, if (m) 0.15f else 0.85f) }
//		tertiaryContainer.toHsl()
//			.let { tertiaryContainer = Color.hsl(h ?: it.hue, it.saturation, if (m) 0.20f else 0.80f) }
//		background.toHsl()
//			.let { background = Color.hsl(h ?: it.hue, it.saturation, if (m) 0.08f else 0.92f) }
//		accent.toHsl()
//			.let { accent = Color.hsl(h ?: it.hue, it.saturation, it.lightness) }
//
//		return this
//	}
//
//}

//data class Hsl(
//	val hue: Float,
//	val saturation: Float,
//	val lightness: Float,
//)

//fun Color.toHsl(): Hsl {
//	val max = max(max(red, green), blue)
//	val min = min(min(red, green), blue)
//
//	val lightness = (min + max) / 2f
//
//	val saturation = if (lightness == 0f || lightness == 1f) 0f
//	else (max - min) / (1f - abs(2f * lightness - 1f))
//
//	val hue = (if (min == max) 0f
//	else if (max == green) (blue - red) / (max - min) + 2f
//	else if (max == blue) (red - green) / (max - min) + 4f
//	else (green - blue) / (max - min) + (if (green >= blue) 6f else 0f)) / 6f
//
//	return Hsl(hue, saturation, lightness)
//}

fun textColorFor(c: Color): Color {
	return if (c.luminance() > 0.5f) Color.Black
	else Color.White
}

// Material Color
val primaryLight = Color(0xFF8F4C38)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDBD1)
val onPrimaryContainerLight = Color(0xFF3A0B01)
val secondaryLight = Color(0xFF77574E)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFDBD1)
val onSecondaryContainerLight = Color(0xFF2C150F)
val tertiaryLight = Color(0xFF6C5D2F)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFF5E1A7)
val onTertiaryContainerLight = Color(0xFF231B00)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFFF8F6)
val onBackgroundLight = Color(0xFF231917)
val surfaceLight = Color(0xFFFFF8F6)
val onSurfaceLight = Color(0xFF231917)
val surfaceVariantLight = Color(0xFFF5DED8)
val onSurfaceVariantLight = Color(0xFF53433F)
val outlineLight = Color(0xFF85736E)
val outlineVariantLight = Color(0xFFD8C2BC)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF392E2B)
val inverseOnSurfaceLight = Color(0xFFFFEDE8)
val inversePrimaryLight = Color(0xFFFFB5A0)
val surfaceDimLight = Color(0xFFE8D6D2)
val surfaceBrightLight = Color(0xFFFFF8F6)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1ED)
val surfaceContainerLight = Color(0xFFFCEAE5)
val surfaceContainerHighLight = Color(0xFFF7E4E0)
val surfaceContainerHighestLight = Color(0xFFF1DFDA)

val primaryDark = Color(0xFFFFB5A0)
val onPrimaryDark = Color(0xFF561F0F)
val primaryContainerDark = Color(0xFF723523)
val onPrimaryContainerDark = Color(0xFFFFDBD1)
val secondaryDark = Color(0xFFE7BDB2)
val onSecondaryDark = Color(0xFF442A22)
val secondaryContainerDark = Color(0xFF5D4037)
val onSecondaryContainerDark = Color(0xFFFFDBD1)
val tertiaryDark = Color(0xFFD8C58D)
val onTertiaryDark = Color(0xFF3B2F05)
val tertiaryContainerDark = Color(0xFF534619)
val onTertiaryContainerDark = Color(0xFFF5E1A7)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF1A110F)
val onBackgroundDark = Color(0xFFF1DFDA)
val surfaceDark = Color(0xFF1A110F)
val onSurfaceDark = Color(0xFFF1DFDA)
val surfaceVariantDark = Color(0xFF53433F)
val onSurfaceVariantDark = Color(0xFFD8C2BC)
val outlineDark = Color(0xFFA08C87)
val outlineVariantDark = Color(0xFF53433F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFF1DFDA)
val inverseOnSurfaceDark = Color(0xFF392E2B)
val inversePrimaryDark = Color(0xFF8F4C38)
val surfaceDimDark = Color(0xFF1A110F)
val surfaceBrightDark = Color(0xFF423734)
val surfaceContainerLowestDark = Color(0xFF140C0A)
val surfaceContainerLowDark = Color(0xFF231917)
val surfaceContainerDark = Color(0xFF271D1B)
val surfaceContainerHighDark = Color(0xFF322825)
val surfaceContainerHighestDark = Color(0xFF3D322F)
