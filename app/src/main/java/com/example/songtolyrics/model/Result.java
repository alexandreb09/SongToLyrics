
package com.example.songtolyrics.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Result {

    @SerializedName("artist")
    @Expose
    private Artist artist;

    @SerializedName("track")
    @Expose
    private Track track;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * @param track
     */
    public Result(Artist artist, Track track) {
        super();
        this.artist = artist;
        this.track = track;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    @Override
    public String toString() {
        Track chanson = getTrack();
        return chanson.getText();
    }
}
