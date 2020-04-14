package com.example.songtolyrics.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseOrionSuggestion {

    private boolean success;


    @SerializedName(value = "result")
    private List<Music> musics;

    public ResponseOrionSuggestion(boolean success, List<Music> musics) {
        this.success = success;
        this.musics = musics;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Music> getMusics() {
        return musics;
    }
}
