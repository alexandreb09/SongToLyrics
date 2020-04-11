package com.example.songtolyrics;

public class Parameters {
    // Requests codes
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static final int REQUEST_MEMORY_PERMISSION = 50;

    public static final int SOURCE_SPOTIFY          = 1;
    public static final int SOURCE_LOCAL_STORAGE    = 2;
    public static final int SOURCE_SUGGESTION       = 3;
    public static final int SOURCE_FAVORITE         = 4;


    public static final String NOT_AVAILABLE = "Non disponible";

    // In seconds
    public static final int REQUEST_TIMEOUT = 10;

    // Connection to the PYTHON API
    public static final String API_PYTHON_URL       = "192.168.1.52";
    public static final String API_PYTHON_PORT      = "5000";
    public static final String API_PYTHON_METHOD    = "find_sound";
    public static final String API_PYTHON_METHOD_TEST_CONNEXION = "check_connexion";
    static final String API_PYTHON_FILENAME         = "audiorecord.3gp";
    public static final int RECCORD_TIME            = 5;        // seconds


    // SPOTIFY API
    public static final int SPOTIFY_REQUEST_CODE            = 1337;
    public static final String SPOTIFY_URL_RECENTLY_PLAYED  = "https://api.spotify.com/v1/me/player/recently-played";
    public static final String SPOTIFY_USER_NAME            = "SPOTIFY_USER_NAME";
    public static final String SPOTIFY_CLIENT_ID            = "018b237a3da84c11b73d0501ea09327f";
    public static final String SPOTIFY_REDIRECT_URI         = "com.example.songtolyrics://callback";
    public static final String SPOTIFY_SCOPES               = "user-read-recently-played";


    // ORION API
    public static final String ORION_API_URL_SUGGESTION = "https://orion.apiseeds.com/api/music/search/?q=";
    public static final String ORION_API_URL_LYRICS = "https://orion.apiseeds.com/api/music/lyric/";
    public static final String ORION_API_KEY = "veVIEjZjElfDNV4XmWIsraUP9Cuu6Otfp5AUyKQVTG0E1Xx9xMFFDwR7odUzEbAW";

    // Local Storage
    public static final String STORAGE_HISTORY = "STORAGE_HISTORY";
    public static final String STORAGE_FAVORITES = "STORAGE_FAVORITES";
    public static final String STORAGE_PYTHON_SERVER_KEY = "STORAGE_PYTHON_SERVER_KEY";
    public static final String DATA = "DATA";
    public static final String DATA_SPOTIFY = "DATA_SPOTIFY";

    public static final int SIZE_MAX_HISTORY = 100;
    public static final String SIZE_MAX_HISTORY_KEY = "SIZE_MAX_HISTORY";
}

