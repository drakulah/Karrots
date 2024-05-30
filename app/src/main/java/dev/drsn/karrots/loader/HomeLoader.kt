package dev.drsn.karrots.loader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.drsn.karrots.LocalInnertube
import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.parser.HomeChip
import dev.drsn.karrots.innertube.parser.HomeListContainer
import dev.drsn.karrots.innertube.routes.home
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeLoader(
	private val loadAll: Boolean = false,
	private val innertube: Innertube
) : ViewModel() {

	private var nextContinuation: String? = null
	private var loadingNextContinuation: String? = null

	var activeChipIndex by mutableStateOf<Int?>(null)
		private set

	val chips = mutableStateListOf<HomeChip>()
	val contents = mutableStateListOf<HomeListContainer>()

	init {
		viewModelScope.launch {
			val homeContent = innertube.home() ?: return@launch

			chips.addAll(homeContent.chips)
			contents.addAll(homeContent.contents)

			nextContinuation = homeContent.continuation

			if (loadAll) while (nextContinuation != null) loadNextContinuation().await()
		}
	}

	fun loadNextContinuation(): Deferred<Unit> {
		return viewModelScope.async {
			if (nextContinuation == null || nextContinuation == loadingNextContinuation) return@async

			loadingNextContinuation = nextContinuation
			val continuationContent = innertube.home(nextContinuation) ?: return@async

			contents.addAll(continuationContent.contents)

			nextContinuation = continuationContent.continuation
			loadingNextContinuation = null
		}
	}

	fun loadFromChipIndex(index: Int) {
		viewModelScope.launch {
			if (index < 0 || index >= chips.size) return@launch

			activeChipIndex = index

			val continuationContent = innertube.home(chips[index].continuation) ?: return@launch

			activeChipIndex = null
//			chips.value = continuationContent.chips

			contents.clear()
			contents.addAll(continuationContent.contents)

			nextContinuation = continuationContent.continuation
		}
	}
}

@Composable
fun rememberHomeLoaderState(loadAll: Boolean = false): HomeLoader {
	val innertube = LocalInnertube.current

	return remember {
		HomeLoader(
			loadAll = loadAll,
			innertube = innertube
		)
	}
}