
package com.example.songtolyrics.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Track {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("lang")
    @Expose
    private Lang lang;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Track() {
    }

    /**
     * 
     * @param name
     * @param text
     * @param lang
     */
    public Track(String name, String text, Lang lang) {
        super();
        this.name = name;
        this.text = text;
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }
}
