package com.example.songtolyrics.model;

import android.content.Context;

import com.example.songtolyrics.R;
import com.google.gson.annotations.SerializedName;

import static com.example.songtolyrics.Parameters.NOT_AVAILABLE;

public class Music implements Comparable<Music>{

    @SerializedName(value="title")
    private String title;

    @SerializedName(value="artist")
    private String artist;

    private boolean alreadySearch;
    private String lyrics;

    public Music(String title, String artist) {
        this.title = cleanTitle(title);
        this.artist = artist;
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music(Song song, Artist artist) {
        this.title = cleanTitle(song.getName());
        this.artist = artist.getName();
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music(Song song) {
        this.title = cleanTitle(song.getName());
        this.artist = NOT_AVAILABLE;
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music() {
        this.title = "";
        this.artist = NOT_AVAILABLE;
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music(ResponseOrionLyrics responseOrionLyrics) {
        this.artist = responseOrionLyrics.getArtist().getName();
        this.title = responseOrionLyrics.getSong().getName();
        this.lyrics = responseOrionLyrics.getSong().getText();
        this.alreadySearch = false;
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
        return null != this.lyrics && !this.lyrics.isEmpty();
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyricsAvailableString(Context context){
        if (isAlreadySearch()){
            if (isLyricsAvailable()){
                return context.getResources().getString(R.string.message_lyrics_available);
            }else{
                return context.getResources().getString(R.string.message_lyrics_unavailable);
            }
        }else{
            return "";
        }

    }

    public void setAlreadySearch(boolean alreadySearch) {
        this.alreadySearch = alreadySearch;
    }
    public boolean isAlreadySearch() {
        return alreadySearch;
    }

    @Override
    public int compareTo(Music o) {
        return title.toLowerCase().compareTo(o.getTitle().toLowerCase());
    }
}
