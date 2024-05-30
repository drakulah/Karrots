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
import dev.drsn.karrots.innertube.parser.PlaylistContinuation
import dev.drsn.karrots.innertube.parser.PlaylistInfo
import dev.drsn.karrots.innertube.parser.PlaylistListContainer
import dev.drsn.karrots.innertube.parser.partial.chunk.Menu
import dev.drsn.karrots.innertube.parser.partial.chunk.ThumbnailInfo
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.parser.partial.preview.Uploader
import dev.drsn.karrots.innertube.routes.playlist
import kotlinx.coroutines.launch

class PlaylistLoader(
	private val browseId: String,
	private val innertube: Innertube
) : ViewModel() {

	private var loadingContinuation: String? = null

	private var tracksContinuation: String? = null
	private var othersContinuation: String? = null

	var title by mutableStateOf("")
		private set
	var description by mutableStateOf<String?>(null)
		private set
	var yearText by mutableStateOf<String?>(null)
		private set
	var trackCount by mutableStateOf<String?>(null)
		private set
	var viewCount by mutableStateOf<String?>(null)
		private set
	var playlistDuration by mutableStateOf<String?>(null)
		private set

	var menu = mutableStateListOf<Menu>()
		private set
	var uploaders = mutableStateListOf<Uploader>()
		private set
	var thumbnail = mutableStateListOf<ThumbnailInfo>()
		private set

	var tracks = mutableStateListOf<TrackPreview>()
		private set
	var others = mutableStateListOf<PlaylistListContainer>()
		private set

	init {
		viewModelScope.launch {
			val info = innertube.playlist(browseId) ?: return@launch

			if (info !is PlaylistInfo) return@launch

			title = info.title
			yearText = info.yearText
			viewCount = info.viewCount
			trackCount = info.trackCount
			description = info.description
			playlistDuration = info.playlistDuration

			menu.addAll(info.menu)
			uploaders.addAll(info.uploaders)
			thumbnail.addAll(info.thumbnail)

			tracks.addAll(info.track)
			others.addAll(info.others)

			tracksContinuation = info.tracksContinuation
			othersContinuation = info.othersContinuation
		}
	}

	fun loadNextContinuation() {
		viewModelScope.launch {
			val continuation = tracksContinuation ?: othersContinuation

			if (
				(loadingContinuation != null && (loadingContinuation == othersContinuation || loadingContinuation == tracksContinuation))
				|| continuation == null
			) return@launch

			loadingContinuation = continuation
			val continuationContent = innertube.playlist(continuation = continuation)

			if (continuationContent !is PlaylistContinuation) return@launch

			tracks.addAll(continuationContent.track)
			others.addAll(continuationContent.others)

			tracksContinuation = continuationContent.tracksContinuation
			if (loadingContinuation == othersContinuation) othersContinuation = null
			loadingContinuation = null
		}
	}

}

@Composable
fun rememberPlaylistLoaderState(browseId: String): PlaylistLoader {
	val innertube = LocalInnertube.current

	return remember {
		PlaylistLoader(
			browseId = browseId,
			innertube = innertube
		)
	}
}