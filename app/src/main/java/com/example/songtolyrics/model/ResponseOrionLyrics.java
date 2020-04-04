package com.example.songtolyrics.model;


import com.google.gson.annotations.SerializedName;

public class ResponseOrionLyrics {

    private String error;
    private Artist artist;

    @SerializedName(value = "track")
    private Song song;


    // ============================================== //
    //              GETTER - SETTER                   //
    // ============================================== //
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public boolean isValid(){
        return error.isEmpty();
    }

}
