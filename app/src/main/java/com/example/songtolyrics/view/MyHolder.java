package com.example.songtolyrics.view;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.songtolyrics.R;


public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ItemClickListener itemClickListener;
    TextView title;
    TextView artist;
    TextView lyricsAvailable;
    ImageView delete;

    public MyHolder(View itemView) {
        super(itemView);

        title           = itemView.findViewById(R.id.song_title);
        artist          = itemView.findViewById(R.id.song_artist);
        lyricsAvailable = itemView.findViewById(R.id.song_lyrics_available);
        delete          = itemView.findViewById(R.id.delete_favorite);


        delete.setOnClickListener(view -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(view, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener)    {
        this.itemClickListener = itemClickListener;
    }
}
