package dev.drsn.karrots.ui.component

//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.unit.dp
//import dev.drsn.karrots.ui.theme.LocalTheme
//import dev.drsn.karrots.ui.theme.textColorFor
//
//@Composable
//fun Button(
//	modifier: Modifier = Modifier,
//	active: Boolean = true,
//	enabled: Boolean = true,
//	leadingIcon: ImageVector? = null,
//	trailingIcon: ImageVector? = null,
//	text: String,
//	onClick: () -> Unit,
//) {
//
//	val theme = LocalTheme.current
//
//	Layer(
//		modifier = Modifier
//			.clip(RoundedCornerShape(8.dp))
//			.background(if (active) theme.colorScheme.primaryContainer else theme.colorScheme.primaryContainer)
//			.clickable(
//				enabled = enabled,
//				onClick = onClick
//			)
//			.padding(horizontal = 18.dp, vertical = 8.dp)
//	) {
//
//		Row(
//			modifier = modifier
//		) {
//
//			if (leadingIcon != null) Icon(
//				imageVector = leadingIcon,
//				contentDescription = "Leading Icon",
//				tint = if (active) textColorFor(theme.colorScheme.primaryContainer) else theme.colorScheme.secondary
//			)
//
//			Text(
//				text = text,
//				style = theme.typography.labelLarge,
//				color = if (active) textColorFor(theme.colorScheme.primaryContainer) else theme.colorScheme.secondary
//			)
//
//			if (trailingIcon != null) Icon(
//				imageVector = trailingIcon,
//				contentDescription = "Leading Icon",
//				tint = if (active) textColorFor(theme.colorScheme.primaryContainer) else theme.colorScheme.secondary
//			)
//
//		}
//
//	}
//
//}