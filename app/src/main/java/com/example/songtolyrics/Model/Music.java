package com.example.songtolyrics.Model;

public class Music {
    private String title;
    private String artist;

    public Music(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
}
