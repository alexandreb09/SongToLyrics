package com.example.songtolyrics.model;

import com.google.gson.annotations.SerializedName;

public class Lyrics extends Song{

    @SerializedName(value = "lyrics", alternate = {"text"})
    private String lyrics;

    public String getLyrics() {
        return lyrics;
    }
}
