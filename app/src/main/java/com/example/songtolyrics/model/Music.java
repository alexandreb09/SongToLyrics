package com.example.songtolyrics.model;

import com.google.gson.annotations.SerializedName;

public class Music implements Comparable<Music>{
    private final String NO_ARTISTS = "Non disponible";

    @SerializedName(value="title")
    private String title;

    @SerializedName(value="artist")
    private String artist;

    private boolean lyricsAvailable;

    public Music(String title, String artist) {
        this.title = cleanTitle(title);
        this.artist = artist;
    }

    public Music(Song song, Artist artist) {
        this.title = cleanTitle(song.getName());
        this.artist = artist.getName();
    }

    public Music(Song song) {
        this.title = cleanTitle(song.getName());
        this.artist = NO_ARTISTS;
    }
    public Music() {
        this.title = "";
        this.artist = NO_ARTISTS;
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }

    private String cleanTitle(String title){
        return title.replaceAll("\\(.*?\\)", "");
    }


    public boolean isLyricsAvailable() {
        return lyricsAvailable;
    }

    public void setLyricsAvailabe(boolean lyricsAvailable) {
        this.lyricsAvailable = lyricsAvailable;
    }


    @Override
    public int compareTo(Music o) {
        return title.toLowerCase().compareTo(o.getTitle().toLowerCase());
    }
}
