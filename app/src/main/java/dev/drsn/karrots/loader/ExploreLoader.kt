package dev.drsn.karrots.loader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.parser.ExploreListContainer
import dev.drsn.karrots.innertube.routes.explore
import dev.drsn.karrots.util.Random
import kotlinx.coroutines.launch

class ExploreLoader(
	private val innertube: Innertube
) : ViewModel() {
	private var prevReqId = ""
	private var continuation: String? = null
	private var isLoading by mutableStateOf(false)

	private val hasMore get() = continuation != null
	private val _contents = mutableStateListOf<ExploreListContainer>()

	val contents = _contents

	init {
		load()
	}

	fun loadMore() {
		if (!isLoading && hasMore) load()
	}

	fun cleanLoad(loadAll: Boolean = false) {
		_contents.clear()
		load(loadAll)
	}

	fun load(loadAll: Boolean = false) {
		viewModelScope.launch {
			isLoading = true
			val currentReqId = Random.generateCode(10, prevReqId)
			prevReqId = currentReqId
			val res = innertube.explore(continuation) ?: return@launch
			if (prevReqId != currentReqId) return@launch
			continuation = res.continuation
			_contents.addAll(res.contents)
			isLoading = false
			if (loadAll && hasMore) load(true)
		}
	}
}