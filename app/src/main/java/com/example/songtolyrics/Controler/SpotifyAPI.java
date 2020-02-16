package com.example.songtolyrics.Controler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SpotifyAPI {
    private static final String TOKEN = "Bearer BQDQ6kqzCQYyZO7COqRFSFyl4m2DK-YjN-4zULamfCT4wTZfte32UOzZGRVSnxTGxBenSgkuU4gND8LF17iRiY_v5adO9usS6YSZRZ6oSUbumGdyphhIG8Kf3FNb5h6k0hBa_B9whio";
    private static final String URL_BASE = "https://api.spotify.com/v1/search?q=";

    private Context mContext;

    public SpotifyAPI(Context context) throws UnsupportedEncodingException {
        mContext = context;
    }

    public void getData(final String textToSearch){
        RequestQueue queue = Volley.newRequestQueue(mContext);
//        String URL = null;
//        try {
//            URL = URL_BASE + URLEncoder.encode(textToSearch, "UTF-8");
//        } catch (
//                UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        StringRequest myReq = new StringRequest(Request.Method.GET, URL_BASE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("value");
                    Log.e("value in success", String.valueOf(success));

                    Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext, jObj.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(mContext, "Parsing error", Toast.LENGTH_SHORT).show();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            // Header
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", TOKEN);
                params.put("Accept", "application/json");
                return params;
            }

            // Parameters
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("q", textToSearch);
                return params;
            }
        };
        queue.add(myReq);
    }
}
