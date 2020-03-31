package com.example.songtolyrics.model;

import com.google.gson.annotations.SerializedName;

import static com.example.songtolyrics.Parameters.NOT_AVAILABLE;

public class Song {

    @SerializedName(value = "name", alternate = {"title"})
    private String name;

    public Song() {
        this.name = NOT_AVAILABLE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
