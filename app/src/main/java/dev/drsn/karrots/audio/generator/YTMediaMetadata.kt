package dev.drsn.karrots.audio.generator

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_MUSIC
import dev.drsn.karrots.innertube.Innertube
import dev.drsn.karrots.innertube.parser.partial.preview.TrackPreview
import dev.drsn.karrots.innertube.routes.player
import dev.drsn.karrots.innertube.utils.autoJoinToString

suspend fun mediaMetadata(track: TrackPreview): MediaItem? {
	val innertube = Innertube()
	val playerData = innertube.player(track.id)

	if (playerData == null || playerData.status != "OK") return null

	val bestAudioUrl =
		playerData.streams.lastOrNull { e -> e.mimeType == "audio/webm; codecs=\"opus\"" }
			?: return null

	val subTitle = track.uploaders.autoJoinToString { e -> e.title }

	return MediaItem.Builder()
		.setMediaId(track.id)
		.setUri(bestAudioUrl.url)
		.setMediaMetadata(
			MediaMetadata.Builder()
				.setArtworkUri(Uri.parse(track.thumbnails.lastOrNull()?.url))
				.setMediaType(MEDIA_TYPE_MUSIC)
				.setAlbumTitle(track.album?.title)
				.setArtist(subTitle)
				.setDescription(subTitle)
				.setDisplayTitle(track.title)
				.setTitle(track.title)
				.setSubtitle(subTitle)
				.build()
		)
		.build()
}