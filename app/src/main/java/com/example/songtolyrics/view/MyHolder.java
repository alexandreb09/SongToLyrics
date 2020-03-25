package com.example.songtolyrics.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.songtolyrics.R;


public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ItemClickListener itemClickListener;
    TextView title;
    TextView artist;

    public MyHolder(View itemView) {
        super(itemView);

        title          = itemView.findViewById(R.id.song_title);
        artist         = itemView.findViewById(R.id.song_artist);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(view,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener)    {
        this.itemClickListener=itemClickListener;
    }
}
