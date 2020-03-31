package com.example.songtolyrics;

public class Parameters {
    // Requests codes
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    public static final String NOT_AVAILABLE = "Non disponible";

    // Connection to the PYTHON API
    public static final String API_PYTHON_URL = "http://192.168.1.52";
    public static final String API_PYTHON_PORT = "8000";
    public static final String API_PYTHON_METHOD = "find_sound";
    public static final String API_PYTHON_FILENAME = "audiorecord.3gp";

    // SPOTIFY API
    public static final String SPOTIFY_URL_RECENTLY_PLAYED = "https://api.spotify.com/v1/me/player/recently-played";

    // ORION API
    public static final String ORION_API_KEY = "veVIEjZjElfDNV4XmWIsraUP9Cuu6Otfp5AUyKQVTG0E1Xx9xMFFDwR7odUzEbAW";
    public static final String ORION_API_URL_SUGGESTION = "https://orion.apiseeds.com/api/music/search/?q=";
}

