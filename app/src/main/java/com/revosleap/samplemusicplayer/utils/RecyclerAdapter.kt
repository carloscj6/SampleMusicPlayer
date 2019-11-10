package com.revosleap.samplemusicplayer.utils

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.revosleap.samplemusicplayer.R
import com.revosleap.samplemusicplayer.models.Song

class RecyclerAdapter :
        androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var onLongClick: OnLongClick? = null
    private var onSongClicked: SongClicked? = null
    private var songsSelected: SongsSelected? = null
    private var songsList = mutableListOf<Song>()
    private var selectedSongs = mutableListOf<Song>()
    private var selectionModeActive = false

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.track_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val song = songsList[position]
        viewHolder.bind(song, position)
        viewHolder.mainItem.isSelected = selectedSongs.contains(song)
        viewHolder.mainItem.setOnLongClickListener {
            onLongClick?.onSongLongClicked(position)
            if (!selectionModeActive) {
                selectionModeActive = true
            }
            false
        }
        viewHolder.mainItem.setOnClickListener {
            if (!selectionModeActive) {
                onSongClicked?.onSongClicked(song)
            } else {
                if (selectedSongs.contains(song)) {
                    selectedSongs.remove(song)
                    songsSelected?.onSelectSongs(getSelectedSongs())

                } else {
                    selectedSongs.add(song)

                    songsSelected?.onSelectSongs(getSelectedSongs())

                }
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun addSongs(songs: MutableList<Song>) {
        songsList.clear()
        songsList.addAll(songs)
        notifyDataSetChanged()
    }

    fun removeSelection() {
        selectionModeActive = false
        selectedSongs.clear()
        notifyDataSetChanged()
    }

    fun getSelectedSongs(): MutableList<Song> {
        return selectedSongs
    }

    fun updateRemoved(song: Song){
        songsList.remove(song)
        notifyDataSetChanged()
    }

    fun setOnSongClicked(songClick: SongClicked) {
        this.onSongClicked = songClick
    }

    fun setOnLongClick(longClick: OnLongClick) {
        this.onLongClick = longClick
    }

    fun setSongsSelected(selection: SongsSelected) {
        this.songsSelected = selection
    }

    interface SongsSelected {
        fun onSelectSongs(selectedSongs: MutableList<Song>)
    }

    interface SongClicked {
        fun onSongClicked(song: Song)
    }

    interface OnLongClick {
        fun onSongLongClicked(position: Int)
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.textViewSongTitle)
        private val artist: TextView = itemView.findViewById(R.id.textViewArtistName)
        val mainItem: ConstraintLayout = itemView.findViewById(R.id.mainConstraint)
        private var position: Int? = null

        fun bind(song: Song, pos: Int) {
            title.text = song.title
            artist.text = song.artistName
            position = pos

        }


    }
}
