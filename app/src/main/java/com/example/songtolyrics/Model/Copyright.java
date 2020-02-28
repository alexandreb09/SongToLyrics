
package com.example.songtolyrics.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Copyright {

    @SerializedName("notice")
    @Expose
    private String notice;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("text")
    @Expose
    private String text;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Copyright() {
    }

    /**
     * 
     * @param artist
     * @param text
     * @param notice
     */
    public Copyright(String notice, String artist, String text) {
        super();
        this.notice = notice;
        this.artist = artist;
        this.text = text;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



}
