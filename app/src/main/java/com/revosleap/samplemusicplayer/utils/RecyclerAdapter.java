package com.revosleap.samplemusicplayer.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.revosleap.samplemusicplayer.R;
import com.revosleap.samplemusicplayer.models.Song;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public RecyclerAdapter(SongClicked clicked) {
        songClicked = clicked;
    }

    private ArrayList songsList = new ArrayList<Song>();
    private SongClicked songClicked;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Song song= (Song) songsList.get(i);
        viewHolder.bind(song);
        viewHolder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        songClicked.onSongClicked(song);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public void addSongs(ArrayList songs) {
        songsList.clear();
        songsList = songs;
        notifyDataSetChanged();
    }

    public interface SongClicked {
        void onSongClicked(Song song);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title,artist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewSongTitle);
            artist = itemView.findViewById(R.id.textViewArtistName);

        }
        void bind(Song song){
            title.setText(song.title);
            artist.setText(song.artistName);
        }
    }
}
