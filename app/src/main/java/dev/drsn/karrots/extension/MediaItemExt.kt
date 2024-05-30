package dev.drsn.karrots.extension

import androidx.media3.common.MediaItem

fun MediaItem.setMediaId(id: String): MediaItem = buildUpon().setMediaId(id).build()