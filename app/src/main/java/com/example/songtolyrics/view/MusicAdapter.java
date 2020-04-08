package com.example.songtolyrics.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.songtolyrics.fragments.ListMusicFragmentDirections;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.R;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MyHolder> {
    private Context mContext;
    private List<Music> mListMusic;
    private View mFragmentView;

    public MusicAdapter(Context context, List<Music> listeMusic, View view){                                   // Création
        this.mContext       = context;
        this.mListMusic     = listeMusic;
        this.mFragmentView  = view;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song, parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Music music = mListMusic.get(position);                                                     // Récupération produit concerné
        holder.title.setText(music.getTitle());                                                     // Insertion texte dans cellules RecyclerView
        holder.artist.setText(music.getArtist());
        holder.lyricsAvailable.setText(music.getLyricsAvailableString(this.mContext));
        if (music.isLyricsAvailable()){
            holder.lyricsAvailable.setTextColor(mContext.getResources().getColor(R.color.green));
        }

        holder.setItemClickListener((v, pos) -> {                                                   // Lors du click sur l'item
            TextView txt_view_song = v.findViewById(R.id.song_title);
            String song = txt_view_song.getText().toString();
            TextView txt_view_artist = v.findViewById(R.id.song_artist);
            String artist = txt_view_artist.getText().toString();

            ListMusicFragmentDirections.ActionListMusicFragmentToLyricsFragment action =
                    ListMusicFragmentDirections.actionListMusicFragmentToLyricsFragment(artist, song);
            Navigation.findNavController(this.mFragmentView).navigate(action);
        });

    }

    @Override
    public int getItemCount() {
        return mListMusic.size();                                                                   // RecyclerView Size
    }
}
