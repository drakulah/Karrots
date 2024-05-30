package dev.drsn.karrots.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.drsn.karrots.ui.theme.LocalTheme

@Composable
fun SearchField(
	modifier: Modifier = Modifier,
	query: String,
	onQueryChange: (String) -> Unit,
	onSearch: (String) -> Unit,
	isActive: Boolean,
	onActiveChange: (Boolean) -> Unit,
	placeholder: String,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	searchBarHeight: Dp = 44.dp,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {

	val theme = LocalTheme.current
	val focusManager = LocalFocusManager.current

	val focusRequester = remember { FocusRequester() }

	LaunchedEffect(isActive) {
		if (!isActive) {
			focusManager.clearFocus()
		}
	}

	Layer(
		modifier = modifier
			.fillMaxWidth()
			.height(searchBarHeight)
			.clip(RoundedCornerShape(8.dp))
			.background(theme.colorScheme.surfaceContainerHigh)
			.clipToBounds(),
		contentAlignment = Alignment.TopCenter
	) {

		Column(
			modifier = Modifier
				.fillMaxSize()
		) {

			BasicTextField(
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(focusRequester)
					.onFocusChanged {
						if (it.isFocused) onActiveChange(true)
					},
				value = if (isActive) query else placeholder,
				onValueChange = onQueryChange,
				enabled = enabled,
				readOnly = readOnly,
				textStyle = TextStyle.Default.copy(
					fontSize = theme.typography.bodyLarge.fontSize,
					fontWeight = FontWeight.Normal,
					fontFamily = theme.typography.bodyLarge.fontFamily,
					letterSpacing = theme.typography.bodyLarge.letterSpacing,
					lineHeight = theme.typography.bodyLarge.lineHeight,
					color = theme.colorScheme.onSurfaceVariant
				),
				cursorBrush = SolidColor(theme.colorScheme.surfaceTint),
				visualTransformation = VisualTransformation.None,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
				keyboardActions = KeyboardActions(
					onSearch = {
						focusManager.clearFocus()
						onSearch(query)
					}
				),
				interactionSource = interactionSource,
				singleLine = true,
				maxLines = 1,
				decorationBox = { innerTextField ->
					Row(
						modifier = Modifier
							.fillMaxSize()
							.padding(start = 12.dp),
						verticalAlignment = Alignment.CenterVertically
					) {

						Box(
							modifier = Modifier
								.weight(1f)
						) {
							if (query.isEmpty()) Text(
								text = placeholder,
								style = theme.typography.bodyLarge,
								fontWeight = FontWeight.Normal,
								color = theme.colorScheme.onSurfaceVariant
							)
							innerTextField()
						}

						if (query.isNotEmpty() && isActive) IconButton(
							onClick = { onQueryChange("") }
						) {
							Icon(
								modifier = Modifier
									.size(18.dp),
								imageVector = Icons.Sharp.Clear,
								contentDescription = "Clear"
							)
						}

					}
				}
			)

//					DockedSearchBar(
//						modifier = Modifier
//							.height(searchBarHeight),
//						query = query,
//						onQueryChange = onQueryChange,
//						onSearch = onSearch,
//						active = isActive,
//						onActiveChange = onActiveChange,
//						leadingIcon = {
//							IconButton(
//								onClick = { onActiveChange(false) }
//							) {
//
//								Icon(
//									imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
//									contentDescription = "Back"
//								)
//							}
//						},
//						trailingIcon = {
//							if (query.isNotEmpty()) {
//								IconButton(
//									onClick = { onQueryChange("") }
//								) {
//
//									Icon(
//										imageVector = Icons.Sharp.Clear,
//										contentDescription = "Clear"
//									)
//								}
//							}
//						},
//						placeholder = {
//							Text(
//								text = "Search Rabbit!",
//								style = theme.typography.bodyLarge,
//								fontWeight = FontWeight.Normal,
//								color = theme.colorScheme.onSurfaceVariant
//							)
//						},
//						colors = SearchBarDefaults.colors(
//							containerColor = theme.colorScheme.surfaceContainerHigh
//						),
//						content = {}
//					)

		}

	}
}