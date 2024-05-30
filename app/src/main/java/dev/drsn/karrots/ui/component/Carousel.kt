package dev.drsn.karrots.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.drsn.karrots.extension.setSizeParam
import dev.drsn.karrots.ui.theme.LocalTheme
import kotlin.math.absoluteValue

data class CarouselImageStyle(
	val height: Dp,
	val shape: Shape
)

object CarouselDefaults {

	private val defaultHeight: Dp @Composable get() = 360.dp
	private val defaultImageShape: Shape @Composable get() = RoundedCornerShape(12.dp)

	@Composable
	fun defaultImageStyle(
		height: Dp = defaultHeight,
		shape: Shape = defaultImageShape
	): CarouselImageStyle = CarouselImageStyle(
		height = height,
		shape = shape
	)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel(
	modifier: Modifier = Modifier,
	pagerState: PagerState,
	models: List<String?>,
	imageStyle: CarouselImageStyle = CarouselDefaults.defaultImageStyle(),
	contentPadding: PaddingValues = PaddingValues(),
) {

	val theme = LocalTheme.current

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {

		Row(
			modifier = modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
		) {

			HorizontalPager(
				modifier = modifier
					.weight(1f)
					.height(imageStyle.height),
				state = pagerState,
				contentPadding = contentPadding,
			) { page ->

				val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
				val scaleFactor = 0.75f + (1f - 0.75f) * (1f - pageOffset.absoluteValue)
				val alphaFactor = 0.50f + (1f - 0.50f) * (1f - pageOffset.absoluteValue)

				AsyncImage(
					modifier = modifier
						.graphicsLayer {
							scaleX = scaleFactor
							scaleY = scaleFactor
						}
						.width(imageStyle.height)
						.height(imageStyle.height)
						.clip(imageStyle.shape)
						.alpha(alphaFactor.coerceIn(0f, 1f))
						.background(theme.colorScheme.secondaryContainer),
					model = models.getOrNull(page)?.setSizeParam(512),
					contentDescription = "Album Art",
					contentScale = ContentScale.Crop
				)
			}

		}
	}
}