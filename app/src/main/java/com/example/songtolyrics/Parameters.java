package com.example.songtolyrics;

public class Parameters {
    // Requests codes
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static final int REQUEST_MEMORY_PERMISSION = 50;

    public static final String NOT_AVAILABLE = "Non disponible";

    // In seconds
    public static final int REQUEST_TIMEOUT = 10;

    // Connection to the PYTHON API
    public static final String API_PYTHON_URL = "http://192.168.1.52";
    public static final String API_PYTHON_PORT = "5000";
    public static final String API_PYTHON_METHOD = "find_sound";
    static final String API_PYTHON_FILENAME = "audiorecord.3gp";

    // SPOTIFY API
    public static final String SPOTIFY_URL_RECENTLY_PLAYED = "https://api.spotify.com/v1/me/player/recently-played";

    // ORION API
    public static final String ORION_API_URL_SUGGESTION = "https://orion.apiseeds.com/api/music/search/?q=";
    public static final String ORION_API_URL_LYRICS = "https://orion.apiseeds.com/api/music/lyric/";
    public static final String ORION_API_KEY = "veVIEjZjElfDNV4XmWIsraUP9Cuu6Otfp5AUyKQVTG0E1Xx9xMFFDwR7odUzEbAW";

    // Local Storage
    public static final String STORAGE_HISTORY = "STORAGE_HISTORY";
    public static final String DATA = "DATA";

    public static final int SIZE_MAX_HISTORY = 100;
    public static final String SIZE_MAX_HISTORY_KEY = "SIZE_MAX_HISTORY";
}

