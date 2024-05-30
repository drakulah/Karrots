package dev.drsn.karrots.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.drsn.karrots.R

private val jost = FontFamily(
	Font(R.font.jost_thin, weight = FontWeight.Thin),
	Font(R.font.jost_light, weight = FontWeight.Light),
	Font(R.font.jost_regular, weight = FontWeight.Normal),
	Font(R.font.jost_medium, weight = FontWeight.Medium),
	Font(R.font.jost_semibold, weight = FontWeight.SemiBold),
	Font(R.font.jost_bold, weight = FontWeight.Bold),
	Font(R.font.jost_extrabold, weight = FontWeight.ExtraBold),
	Font(R.font.jost_black, weight = FontWeight.Black),
)
private val fontFamily = jost

val typography = Typography(
	displayLarge = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 57.sp,
		lineHeight = 64.sp,
		letterSpacing = (-0.25).sp
	),
	displayMedium = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 45.sp,
		lineHeight = 52.sp,
		letterSpacing = 0.sp
	),
	displaySmall = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 36.sp,
		lineHeight = 44.sp,
		letterSpacing = 0.sp
	),

	headlineLarge = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 32.sp,
		lineHeight = 40.sp,
		letterSpacing = 0.sp
	),
	headlineMedium = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 28.sp,
		lineHeight = 36.sp,
		letterSpacing = 0.sp
	),
	headlineSmall = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 24.sp,
		lineHeight = 32.sp,
		letterSpacing = 0.sp
	),

	titleLarge = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 22.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp
	),
	titleMedium = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.15.sp
	),
	titleSmall = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.1.sp
	),

	labelLarge = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.1.sp
	),
	labelMedium = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp
	),
	labelSmall = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 10.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.sp
	),

	bodyLarge = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.5.sp
	),
	bodyMedium = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.25.sp
	),
	bodySmall = TextStyle(
		fontFamily = fontFamily,
		fontWeight = FontWeight.Normal,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.4.sp
	),
)