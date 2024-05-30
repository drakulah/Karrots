package dev.drsn.karrots.extension

import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_MUSIC
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.utils.autoJoinToString
import dev.drsn.karrots.innertube.utils.joinToString

@OptIn(UnstableApi::class)
fun TrackPreview.toMediaItem(): MediaItem = MediaItem.Builder()
	.setMediaId(id)
	.setUri(id)
	.setCustomCacheKey(id)
	.setMediaMetadata(
		MediaMetadata.Builder()
			.setTitle(title)
			.setSubtitle(uploaders.autoJoinToString { it.title })
			.setArtist(uploaders.autoJoinToString { it.title })
			.setArtworkUri(thumbnails.lastOrNull()?.url?.toUri())
			.setAlbumTitle(album?.title)
			.setMediaType(MEDIA_TYPE_MUSIC)
			.build()
	)
	.setMimeType(MimeTypes.AUDIO_OPUS)
	.build()

fun TrackPreview.generateDesc(): String = listOf(
	uploaders.autoJoinToString { e -> e.title },
	album?.title,
	trackPlays,
	durationText
).joinToString(joiner = " • ") { e -> e ?: "" }