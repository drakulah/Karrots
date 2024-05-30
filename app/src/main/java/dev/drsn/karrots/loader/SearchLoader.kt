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
import dev.drsn.karrots.innertube.parser.SearchListContainer
import dev.drsn.karrots.innertube.parser.SearchParamChip
import dev.drsn.karrots.innertube.parser.SearchTopResult
import dev.drsn.karrots.innertube.parser.partial.preview.PreviewParser
import dev.drsn.karrots.innertube.routes.search
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchLoader(
	private val query: String,
	private val innertube: Innertube
) : ViewModel() {

	private var job: Job? = null
	private var loadingContinuation: String? = null
	private var continuation: String? = null

	var activeChipIndex by mutableStateOf<Int?>(null)

	var previewTopResults by mutableStateOf<SearchTopResult?>(null)
		private set

	var previewContents = mutableStateListOf<SearchListContainer>()
		private set

	var chips = mutableStateListOf<SearchParamChip>()
		private set

	val continuationContents = mutableStateListOf<PreviewParser.ContentPreview>()

	init {
		load()
	}

	private fun load() {
		viewModelScope.launch {
			activeChipIndex = null
			chips.clear()
			previewContents.clear()
			previewTopResults = null
			continuation = null

			val info = innertube.search(query) ?: return@launch

			chips.addAll(info.chips)
			previewContents.addAll(info.contents)
			previewTopResults = info.topResult
		}
	}

	fun loadNextContinuation() {
		if (continuation != null && continuation == loadingContinuation) return
		if (loadingContinuation != null) {
			job?.cancel()
			loadingContinuation = null
		}

		job = viewModelScope.launch {
			if (continuation == null || loadingContinuation != null) return@launch

			loadingContinuation = continuation

			innertube.search(query = null, continuation = continuation)
				?.let { continuationContent ->
					continuation = continuationContent.continuation
					val contents = continuationContent.contents.getOrNull(0) ?: return@let
					continuationContents.addAll(contents.preContents)
				}

			loadingContinuation = null
		}
	}

	fun loadFromChipIndex(index: Int?) {
		job?.cancel()

		loadingContinuation = null
		continuationContents.clear()

		if (index == null) return load()

		job = viewModelScope.launch {
			if (index < 0 || index >= chips.size) return@launch

			activeChipIndex = index
			val searchResult =
				innertube.search(query = query, params = chips[index].continuation) ?: return@launch
			val contents = searchResult.contents.getOrNull(0) ?: return@launch
			continuation = searchResult.continuation
			continuationContents.addAll(contents.preContents)
		}
	}

}

@Composable
fun rememberSearchLoaderState(query: String): SearchLoader {
	val innertube = LocalInnertube.current

	return remember {
		SearchLoader(
			query = query,
			innertube = innertube
		)
	}
}