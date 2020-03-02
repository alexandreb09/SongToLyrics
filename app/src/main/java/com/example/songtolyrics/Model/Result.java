
package com.example.songtolyrics.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Result {

    @SerializedName("artist")
    @Expose
    private Artist artist;
    @SerializedName("track")
    @Expose
    private Track track;
    @SerializedName("copyright")
    @Expose
    private Copyright copyright;
    @SerializedName("probability")
    @Expose
    private Integer probability;
    @SerializedName("similarity")
    @Expose
    private Integer similarity;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param copyright
     * @param artist
     * @param probability
     * @param similarity
     * @param track
     */
    public Result(Artist artist, Track track, Copyright copyright, Integer probability, Integer similarity) {
        super();
        this.artist = artist;
        this.track = track;
        this.copyright = copyright;
        this.probability = probability;
        this.similarity = similarity;
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

    public Copyright getCopyright() {
        return copyright;
    }

    public void setCopyright(Copyright copyright) {
        this.copyright = copyright;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public Integer getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Integer similarity) {
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        Track chanson = getTrack();
        return chanson.getText();
    }
}
