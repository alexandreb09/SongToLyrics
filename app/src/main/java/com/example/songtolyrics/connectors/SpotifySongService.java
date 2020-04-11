package com.example.songtolyrics.connectors;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.songtolyrics.model.Artist;
import com.example.songtolyrics.model.Music;
import com.google.gson.Gson;
import com.example.songtolyrics.model.Song;
import com.example.songtolyrics.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.songtolyrics.Parameters.DATA_SPOTIFY;
import static com.example.songtolyrics.Parameters.SPOTIFY_URL_RECENTLY_PLAYED;

public class SpotifySongService {
    private ArrayList<Music> musics = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SpotifySongService(Context context) {
        sharedPreferences = context.getSharedPreferences(DATA_SPOTIFY, 0);
        queue = Volley.newRequestQueue(context);
    }

    public ArrayList<Music> getSongs() {
        return musics;
    }

    public ArrayList<Music> getRecentlyPlayedTracks(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, SPOTIFY_URL_RECENTLY_PLAYED, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            JSONObject track = object.optJSONObject("track");

                            // Search for songs
                            Song song = gson.fromJson(track.toString(), Song.class);
                            Music music = new Music(song);

                            // Search for artists
                            JSONArray artists = track.optJSONObject("album").optJSONArray("artists");
                            if (artists.length() > 0){
                                // Retrieve 1st artist
                                String artist_str = artists.get(0).toString();
                                music = new Music(song, gson.fromJson(artist_str, Artist.class));
                            }

                            musics.add(music);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error
                    String temp = "Error";
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

        return musics;
    }


//    public void addSongToLibrary(Song song) {
//        JSONObject payload = preparePutPayload(song);
//        JsonObjectRequest jsonObjectRequest = prepareSongLibraryRequest(payload);
//        queue.add(jsonObjectRequest);
//    }
//
//    private JsonObjectRequest prepareSongLibraryRequest(JSONObject payload) {
//        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/tracks", payload, response -> {
//        }, error -> {
//        }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                String token = sharedPreferences.getString("token", "");
//                String auth = "Bearer " + token;
//                headers.put("Authorization", auth);
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//    }
//
//    private JSONObject preparePutPayload(Song song) {
//        JSONArray idarray = new JSONArray();
//        idarray.put(song.getId());
//        JSONObject ids = new JSONObject();
//        try {
//            ids.put("ids", idarray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return ids;
//    }
}
