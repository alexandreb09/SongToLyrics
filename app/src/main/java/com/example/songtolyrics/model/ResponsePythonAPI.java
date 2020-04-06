package com.example.songtolyrics.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ResponsePythonAPI {

    @SerializedName("artist")
    private String artist;

    @SerializedName("title")
    private String title;


    /**
     * No args constructor for use in serialization
     */
    public ResponsePythonAPI() {
    }

    /**
     * @return empty ResponsePythonAPI object
     */
    public ResponsePythonAPI setEmpty(){
        artist = "";
        title = "";
        return this;
    }


    @NonNull
    @Override
    public String toString(){
        return "{\"artist\": " + artist + ", \"title\":" + title + "}";
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}
