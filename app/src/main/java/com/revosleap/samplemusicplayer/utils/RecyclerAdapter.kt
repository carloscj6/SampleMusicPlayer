package com.revosleap.samplemusicplayer.utils

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.revosleap.samplemusicplayer.R
import com.revosleap.samplemusicplayer.models.Song
import java.util.*

class RecyclerAdapter(private val songClicked: SongClicked) :
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var songsList: ArrayList<*> = ArrayList<Song>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.track_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val song = songsList[i] as Song
        viewHolder.bind(song)
        viewHolder.itemView.setOnClickListener { songClicked.onSongClicked(song) }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun addSongs(songs: ArrayList<*>) {
        songsList.clear()
        songsList = songs
        notifyDataSetChanged()
    }

    interface SongClicked {
        fun onSongClicked(song: Song)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView
        private val artist: TextView

        init {
            title = itemView.findViewById(R.id.textViewSongTitle)
            artist = itemView.findViewById(R.id.textViewArtistName)

        }

        fun bind(song: Song) {
            title.text = song.title
            artist.text = song.artistName
        }
    }
}
