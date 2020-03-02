
package com.example.songtolyrics.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Artist {

    @SerializedName("name")
    @Expose
    private String name;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Artist() {
    }

    /**
     * 
     * @param name
     */
    public Artist(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
