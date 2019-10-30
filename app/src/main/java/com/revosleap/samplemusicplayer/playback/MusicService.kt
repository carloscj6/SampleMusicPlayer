package com.revosleap.samplemusicplayer.playback

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {
    private val mIBinder = LocalBinder()

    var mediaPlayerHolder: MediaPlayerHolder? = null
        private set

    var musicNotificationManager: MusicNotificationManager? = null
        private set

    var isRestoredFromPause = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayerHolder!!.registerNotificationActionsReceiver(false)
        musicNotificationManager = null
        mediaPlayerHolder!!.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        if (mediaPlayerHolder == null) {
            mediaPlayerHolder = MediaPlayerHolder(this)
            musicNotificationManager = MusicNotificationManager(this)
            mediaPlayerHolder!!.registerNotificationActionsReceiver(true)
        }
        return mIBinder
    }

    inner class LocalBinder : Binder() {
        val instance: MusicService
            get() = this@MusicService
    }
}
