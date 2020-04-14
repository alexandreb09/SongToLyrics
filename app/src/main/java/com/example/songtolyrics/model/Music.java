package com.example.songtolyrics.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.songtolyrics.R;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

import static com.example.songtolyrics.Parameters.NOT_AVAILABLE;


public class Music implements Comparable<Music>, Parcelable {

    @SerializedName(value="title")
    private String title;

    @SerializedName(value="artist")
    private String artist;

    private boolean alreadySearch;
    private String lyrics;

    public Music(String title, String artist) {
        this.artist = artist.replaceAll("-", "");
        cleanAndSetTitle(title.replaceAll("-", ""));
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music(@NonNull Song song, @NonNull Artist artist) {
        this.setArtist(artist.getName());
        cleanAndSetTitle(song.getName());
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music(@NonNull Song song) {
        this.artist = NOT_AVAILABLE;
        this.setTitle(song.getName());
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music() {
        this.title = "";
        this.artist = NOT_AVAILABLE;
        this.lyrics = "";
        this.alreadySearch = false;
    }
    public Music(@NonNull ResponseOrionLyrics responseOrionLyrics) {
        this.setArtist(responseOrionLyrics.getArtist().getName());
        this.setTitle(responseOrionLyrics.getSong().getName());
        this.lyrics = responseOrionLyrics.getSong().getText();
        this.alreadySearch = false;
    }

    public void setTitle(String title_){
        this.title = capitalizeAll(title_);
    }
    public String getTitle(){
        return title;
    }
    public void setArtist(String artist_){ this.artist = capitalizeAll(artist_);}
    public String getArtist(){
        return artist;
    }
    public boolean isLyricsAvailable() {
        return !(null == this.lyrics || this.lyrics.isEmpty());
    }
    public String getLyrics() {
        return lyrics;
    }
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
    public void setAlreadySearch(boolean alreadySearch) {
        this.alreadySearch = alreadySearch;
    }
    public boolean isAlreadySearch() {
        return alreadySearch;
    }

    public String getLyricsAvailableString(Context context){
        if (isAlreadySearch()){
            if (isLyricsAvailable()){
                return context.getResources().getString(R.string.txt_lyrics_available);
            }else{
                return context.getResources().getString(R.string.txt_lyrics_unavailable);
            }
        }else{
            return "";
        }

    }

    private void cleanAndSetTitle(@NonNull String title){
        List<String> result = new ArrayList<>();
        String proper = title.toLowerCase()                                                         // Lowercase
                .replaceAll("\\(.*?\\)", "")                                        // Word between "()"
                .replaceAll("\\[(.*?)\\]", "")                                      // Word between "[]"
                .replaceAll("_", "-")                                               // Change _ to -
                .replaceAll("É", "E");                                              // Change É to E

        String[] temp = proper.split("-");                                                    // Split by -
        if (1 == temp.length){
            temp = proper.split("\\s{2,}");
        }

        for (String s : temp) {
            if (!s.matches("^\\s*$") && !s.matches(".*\\b(lyrics|official|officiel|vidéo|video|avec|paroles|mix|radio|[0-9]{4})\\b.*")) {
                result.add(s.replaceAll("^(\\s+)", "").replaceAll("(\\s+)$", ""));
            }
        }

        if (result.isEmpty()) result.add(proper);

        if ((this.getArtist().equals(NOT_AVAILABLE) || this.getArtist().isEmpty()) && 2 == result.size()){
            this.setArtist(result.get(0));
            // If the title contains "ft" or "feat" -> remove all following
            int idx = ftIndex(result.get(1));
            this.setTitle(idx >= 0 ? result.get(1).substring(0, idx) : result.get(1));
        }else{
            StringBuilder title_cleaned = new StringBuilder();
            for (String s: result){
                title_cleaned.append(s).append(" - ");
            }
            title_cleaned = new StringBuilder(title_cleaned.substring(0, title_cleaned.length() - 3));
            this.setTitle(title_cleaned.toString());
        }
    }

    private static int ftIndex(String s) {
        Matcher matcher = Pattern.compile("(ft|feat|ft.)").matcher(s);
        return matcher.find() ? matcher.start() : -1;
    }

    private String capitalizeAll(String str){
        StringBuilder out = new StringBuilder();
        for (String s: str.split(" ")){
            out.append(StringUtils.capitalize(s)).append(" ");
        }
        return out.toString();
    }

    @Override
    public int compareTo(Music o) {
        return title.toLowerCase().compareTo(o.getTitle().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Music))return false;
        Music o_m = (Music) o;
        return (getTitle() + getArtist()).toLowerCase().equals((o_m.getTitle() + o_m.getArtist()).toLowerCase());
    }

    // PARCEL PART

    // Parcelling part
    public Music(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.title = data[0];
        this.artist = data[1];
        this.lyrics = data[2];
        this.alreadySearch = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringArray(new String[] {getTitle(),getArtist(), getLyrics()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
