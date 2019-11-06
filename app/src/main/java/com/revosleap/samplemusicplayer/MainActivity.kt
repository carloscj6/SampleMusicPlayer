package com.revosleap.samplemusicplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.revosleap.samplemusicplayer.models.Song
import com.revosleap.samplemusicplayer.playback.MusicNotificationManager
import com.revosleap.samplemusicplayer.playback.MusicService
import com.revosleap.samplemusicplayer.playback.PlaybackInfoListener
import com.revosleap.samplemusicplayer.playback.PlayerAdapter
import com.revosleap.samplemusicplayer.utils.EqualizerUtils
import com.revosleap.samplemusicplayer.utils.RecyclerAdapter
import com.revosleap.samplemusicplayer.utils.SongProvider
import com.revosleap.samplemusicplayer.utils.Utils
import kotlinx.android.synthetic.main.controls.*

import java.util.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener, RecyclerAdapter.SongClicked {

    private var recyclerView: RecyclerView? = null
    private var seekBar: SeekBar? = null
    private var playPause: ImageButton? = null
    private var next: ImageButton? = null
    private var previous: ImageButton? = null
    private var songTitle: TextView? = null
    private var mMusicService: MusicService? = null
    private var mIsBound: Boolean? = null
    private var mPlayerAdapter: PlayerAdapter? = null
    private var mUserIsSeeking = false
    private var mPlaybackListener: PlaybackListener? = null
    private var mSelectedArtistSongs: List<Song>? = null
    private var mMusicNotificationManager: MusicNotificationManager? = null
    private var recyclerAdapter: RecyclerAdapter? = null
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            mMusicService = (iBinder as MusicService.LocalBinder).instance
            mPlayerAdapter = mMusicService!!.mediaPlayerHolder
            mMusicNotificationManager = mMusicService!!.musicNotificationManager

            if (mPlaybackListener == null) {
                mPlaybackListener = PlaybackListener()
                mPlayerAdapter!!.setPlaybackInfoListener(mPlaybackListener!!)
            }
            if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {

                restorePlayerStatus()
            }
            checkReadStoragePermissions()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mMusicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doBindService()
        setViews()
        initializeSeekBar()

    }

    override fun onPause() {
        super.onPause()
        doUnbindService()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {
            mPlayerAdapter!!.onPauseActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        doBindService()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {

            restorePlayerStatus()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setViews() {
        playPause = findViewById(R.id.buttonPlayPause)
        next = findViewById(R.id.buttonNext)
        previous = findViewById(R.id.buttonPrevious)
        seekBar = findViewById(R.id.seekBar)
        recyclerView = findViewById(R.id.recyclerView)
        songTitle = findViewById(R.id.songTitle)
        //To listen to clicks
        playPause!!.setOnClickListener(this)
        next!!.setOnClickListener(this)
        previous!!.setOnClickListener(this)
        //set adapter
        recyclerAdapter = RecyclerAdapter(this)
        recyclerView!!.adapter = recyclerAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.setHasFixedSize(true)
        //get songs
        mSelectedArtistSongs = SongProvider.getAllDeviceSongs(this)
        recyclerAdapter!!.addSongs((mSelectedArtistSongs as ArrayList<*>?)!!)
    }

    private fun checkReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    private fun updatePlayingInfo(restore: Boolean, startPlay: Boolean) {

        if (startPlay) {
            mPlayerAdapter!!.getMediaPlayer()?.start()
            Handler().postDelayed({
                mMusicService!!.startForeground(MusicNotificationManager.NOTIFICATION_ID,
                        mMusicNotificationManager!!.createNotification())
            }, 200)
        }

        val selectedSong = mPlayerAdapter!!.getCurrentSong()

        songTitle?.text = selectedSong?.title
        val duration = selectedSong?.duration
        seekBar?.max = duration!!
        imageViewControl?.setImageBitmap(Utils.songArt(selectedSong.path!!,this@MainActivity))

        if (restore) {
            seekBar!!.progress = mPlayerAdapter!!.getPlayerPosition()
            updatePlayingStatus()


            Handler().postDelayed({
                //stop foreground if coming from pause state
                if (mMusicService!!.isRestoredFromPause) {
                    mMusicService!!.stopForeground(false)
                    mMusicService!!.musicNotificationManager!!.notificationManager
                            .notify(MusicNotificationManager.NOTIFICATION_ID,
                                    mMusicService!!.musicNotificationManager!!.notificationBuilder!!.build())
                    mMusicService!!.isRestoredFromPause = false
                }
            }, 250)
        }
    }


    private fun updatePlayingStatus() {
        val drawable = if (mPlayerAdapter!!.getState() != PlaybackInfoListener.State.PAUSED)
            R.drawable.ic_pause
        else
            R.drawable.ic_play
        playPause!!.post { playPause!!.setImageResource(drawable) }
    }

    private fun restorePlayerStatus() {
        seekBar!!.isEnabled = mPlayerAdapter!!.isMediaPlayer()

        //if we are playing and the activity was restarted
        //update the controls panel
        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {

            mPlayerAdapter!!.onResumeActivity()
            updatePlayingInfo(true, false)
        }
    }

    private fun doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(Intent(this,
                MusicService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        mIsBound = true

        val startNotStickyIntent = Intent(this, MusicService::class.java)
        startService(startNotStickyIntent)
    }

    private fun doUnbindService() {
        if (mIsBound!!) {
            // Detach our existing connection.
            unbindService(mConnection)
            mIsBound = false
        }
    }

    fun onSongSelected(song: Song, songs: List<Song>) {
        if (!seekBar!!.isEnabled) {
            seekBar!!.isEnabled = true
        }
        try {
            mPlayerAdapter!!.setCurrentSong(song, songs)
            mPlayerAdapter!!.initMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun skipPrev() {
        if (checkIsPlayer()) {
            mPlayerAdapter!!.instantReset()
        }
    }

    fun resumeOrPause() {
        if (checkIsPlayer()) {
            mPlayerAdapter!!.resumeOrPause()
        }
    }

    fun skipNext() {
        if (checkIsPlayer()) {
            mPlayerAdapter!!.skip(true)
        }
    }

    private fun checkIsPlayer(): Boolean {

        val isPlayer = mPlayerAdapter!!.isMediaPlayer()
        if (!isPlayer) {
            EqualizerUtils.notifyNoSessionId(this)
        }
        return isPlayer
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonPlayPause -> {
                resumeOrPause()

            }
            R.id.buttonNext -> {
                skipNext()
            }
            R.id.buttonPrevious -> {
                skipPrev()
            }
        }
    }

    private fun initializeSeekBar() {
        seekBar!!.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    internal var userSelectedPosition = 0

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = true
                    }

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                        if (fromUser) {
                            userSelectedPosition = progress

                        }

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                        if (mUserIsSeeking) {

                        }
                        mUserIsSeeking = false
                        mPlayerAdapter!!.seekTo(userSelectedPosition)
                    }
                })
    }


    override fun onSongClicked(song: Song) {
        onSongSelected(song, mSelectedArtistSongs!!)
    }


    internal inner class PlaybackListener : PlaybackInfoListener() {

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                seekBar!!.progress = position
            }
        }

        override fun onStateChanged(@State state: Int) {

            updatePlayingStatus()
            if (mPlayerAdapter!!.getState() != State.PAUSED
                    && mPlayerAdapter!!.getState() != State.PAUSED) {
                updatePlayingInfo(false, true)
            }
        }

        override fun onPlaybackCompleted() {
            //After playback is complete
        }
    }
}
