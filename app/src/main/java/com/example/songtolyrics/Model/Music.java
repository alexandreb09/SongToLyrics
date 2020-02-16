package com.example.songtolyrics.Model;

public class Music implements Comparable<Music>{
    private String title;
    private String artist;

    public Music(String title, String artist) {
        this.title = cleanTitle(title);
        this.artist = artist;
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

    @Override
    public int compareTo(Music o) {
        return artist.compareTo(o.getArtist());
    }
}
