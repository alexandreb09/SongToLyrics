package com.example.songtolyrics.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.R;
import com.example.songtolyrics.controler.LyricsActivity;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MyHolder> {
    private Context mContext;
    private List<Music> mListMusic;

    public MusicAdapter(Context context, List<Music> listeMusic){                                  // Création
        this.mContext = context;
        this.mListMusic = listeMusic;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song, parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Music music = mListMusic.get(position);                                         // Récupération produit concerné
        holder.title.setText(music.getTitle());                                           // Insertion texte dans cellules RecyclerView
        holder.artist.setText(music.getArtist());

        holder.setItemClickListener((v, pos) -> {                                                   // Lors du click sur l'item
            // Add parameters to activity
            Bundle b = new Bundle();
            TextView t = v.findViewById(R.id.song_title);
            b.putString("title", t.getText().toString());
            TextView textViewArtist = v.findViewById(R.id.song_artist);
            b.putString("artist", textViewArtist.getText().toString());

            Intent nouvelle_recherche = new Intent(mContext, LyricsActivity.class);                 // Create new activity: search lyrics
            nouvelle_recherche.putExtras(b);                                                        // Add parameters to activity
            mContext.startActivity(nouvelle_recherche);                                             // Run activity
        });

    }

    @Override
    public int getItemCount() {
        return mListMusic.size();                                                                   // RecyclerView Size
    }
}
