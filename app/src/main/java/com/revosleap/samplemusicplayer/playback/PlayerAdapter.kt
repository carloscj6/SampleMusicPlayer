package com.revosleap.samplemusicplayer.playback

import android.media.MediaPlayer

import com.revosleap.samplemusicplayer.models.Song

interface PlayerAdapter {

    val isMediaPlayer: Boolean

    val isPlaying: Boolean

    val isReset: Boolean

    val currentSong: Song

    @get:PlaybackInfoListener.State
    val state: Int

    val playerPosition: Int

    val mediaPlayer: MediaPlayer
    fun initMediaPlayer()

    fun release()

    fun resumeOrPause()

    fun reset()

    fun instantReset()

    fun skip(isNext: Boolean)

    fun seekTo(position: Int)

    fun setPlaybackInfoListener(playbackInfoListener: PlaybackInfoListener)

    fun registerNotificationActionsReceiver(isRegister: Boolean)


    fun setCurrentSong(song: Song, songs: List<Song>)

    fun onPauseActivity()

    fun onResumeActivity()
}
