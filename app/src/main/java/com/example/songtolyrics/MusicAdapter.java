package com.example.songtolyrics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Entity.Music;

class MusicAdapter extends BaseAdapter {
    private ArrayList<Music> mMusics;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int mLayout;

    MusicAdapter(Context context, int layout) {
        mLayout = layout;
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMusics = new ArrayList<>();
    }


    void addMusic(Music music){
        mMusics.add(music);
    }

    void setMusicList(ArrayList<Music> listMusic){
        this.mMusics = listMusic;
    }

    public int getCount() {
        return mMusics.size();
    }

    public Music getItem(int position) {
        return mMusics.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if (view == null) {
            view = mLayoutInflater.inflate(mLayout, parent, false);
        }
        ViewHolder holder = (ViewHolder)view.getTag();
        if (holder == null){
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Music music= getItem(position);
        holder.mTextViewArtist.setText(music.getArtist());
        holder.mTextViewTitle.setText(music.getTitle());
        return view;
    }


    private class ViewHolder{
        TextView mTextViewTitle, mTextViewArtist;

        ViewHolder(View rowLayout){
            mTextViewTitle = rowLayout.findViewById(R.id.song_title);
            mTextViewArtist = rowLayout.findViewById(R.id.song_artist);
        }
    }
}
