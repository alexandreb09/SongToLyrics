package com.example.songtolyrics.fragments;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songtolyrics.R;
import com.example.songtolyrics.connectors.SpotifySongService;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.view.MusicAdapter;
import com.example.songtolyrics.view.SimpleDividerItemDecoration;

import java.util.ArrayList;


public class SpotifyListMusicFragment extends BaseFragment {

    private SpotifySongService songService;
    private ArrayList<Music> recentlyPlayedTracks;
    private RecyclerView mRecyclerView;

    // Required empty public constructor
    public SpotifyListMusicFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spotify_list_music, container, false);

        songService = new SpotifySongService(mContext);

        mRecyclerView           = view.findViewById(R.id.list_music_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mContext));

        songService.getRecentlyPlayedTracks(() -> {
            recentlyPlayedTracks = songService.getSongs();
            displaySongs(recentlyPlayedTracks, view);
        });

        return view;
    }

    private void displaySongs(ArrayList<Music> musics, View view) {
        // RECYCLER VIEW
        MusicAdapter mAdapter = new MusicAdapter(mContext, musics, view);
        mRecyclerView.setAdapter(mAdapter);
    }
}
