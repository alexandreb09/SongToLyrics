
package com.example.songtolyrics.model;

import com.google.gson.annotations.SerializedName;


public class Artist {

    @SerializedName(value = "name", alternate = {"artist"})
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
