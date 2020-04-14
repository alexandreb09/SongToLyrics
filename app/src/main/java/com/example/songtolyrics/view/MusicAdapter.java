package com.example.songtolyrics.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.songtolyrics.Utils;
import com.example.songtolyrics.fragments.ListMusicFragmentDirections;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.R;

import java.util.List;

import static com.example.songtolyrics.Parameters.SOURCE_FAVORITE;

public class MusicAdapter extends RecyclerView.Adapter<MyHolder>{
    private Context mContext;
    private List<Music> mListMusic;
    private View mFragmentView;
    private int mSource;

    public MusicAdapter(Context context, List<Music> listeMusic, View view, int source){                                   // Création
        this.mContext       = context;
        this.mListMusic     = listeMusic;
        this.mFragmentView  = view;
        this.mSource        = source;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song, parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (SOURCE_FAVORITE == mSource){
            holder.delete.setVisibility(View.VISIBLE);
        }

        Music music = mListMusic.get(position);                                                     // Récupération produit concerné
        holder.title.setText(music.getTitle());                                                     // Insertion texte dans cellules RecyclerView
        holder.artist.setText(music.getArtist());

        holder.lyricsAvailable.setText(music.getLyricsAvailableString(this.mContext));

        // If this music search has already been performed
        if (music.isAlreadySearch()){
            // Show message if lyrics available
            holder.lyricsAvailable.setText(music.getLyricsAvailableString(mContext));

            // If lyrics unavailable -> grey color
            if (!music.isLyricsAvailable()){
                holder.lyricsAvailable.setTextColor(mContext.getResources().getColor(R.color.grey));
            }
        }

        holder.setItemClickListener((v, pos) -> {
            if (v.getId() == R.id.delete_favorite){
                mListMusic.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mListMusic.size());
                holder.itemView.setVisibility(View.GONE);
                Utils.removeFavoriteAndStore(mContext, music);
            }else{
                TextView txt_view_song = v.findViewById(R.id.song_title);
                String song = txt_view_song.getText().toString();
                TextView txt_view_artist = v.findViewById(R.id.song_artist);
                String artist = txt_view_artist.getText().toString();

                ListMusicFragmentDirections.ActionListMusicFragmentToLyricsFragment action =
                        ListMusicFragmentDirections.actionListMusicFragmentToLyricsFragment(artist, song);
                Navigation.findNavController(this.mFragmentView).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListMusic.size();                                                                   // RecyclerView Size
    }
}
