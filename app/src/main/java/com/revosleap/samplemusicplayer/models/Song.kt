package com.revosleap.samplemusicplayer.models

import java.util.Locale
import java.util.concurrent.TimeUnit

class Song(val title: String, val trackNumber: Int, private val year: Int, val duration: Int,
           val path: String?, val albumName: String, val artistId: Int, val artistName: String) {
    companion object {
        internal val EMPTY_SONG = Song("", -1, -1, -1,
                null, "", -1, "")

        fun formatDuration(duration: Int): String {
            return String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong())))
        }

        fun formatTrack(trackNumber: Int): Int {
            var formatted = trackNumber
            if (trackNumber >= 1000) {
                formatted = trackNumber % 1000
            }
            return formatted
        }
    }
}
