package com.example.songtolyrics.model;

import com.google.gson.annotations.SerializedName;

import static com.example.songtolyrics.Parameters.NOT_AVAILABLE;

public class Song {

    @SerializedName(value = "name", alternate = {"title"})
    private String name;

    private String text;

    public Song() {
        this.name = NOT_AVAILABLE;
        this.text = "";
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
