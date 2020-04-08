package com.example.songtolyrics.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.songtolyrics.R;
import com.example.songtolyrics.connectors.SpotifyUserService;
import com.example.songtolyrics.model.User;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.example.songtolyrics.Parameters.SOURCE_SPOTIFY;


public class SpotifyConnectFragment extends BaseFragment {
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private View mParentView;
    private Button mButtonConnexion;

    private RequestQueue queue;

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "3a7b0154a0fd4a868e41d59834f97bd5";
    private static final String REDIRECT_URI = "com.spotifyapiexample://callback";
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-library-read,playlist-modify-public,playlist-modify-private,user-read-email,user-read-private,user-read-birthdate,playlist-read-private,playlist-read-collaborative";

    // Required empty public constructor
    public SpotifyConnectFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParentView = inflater.inflate(R.layout.fragment_spotify_connect, container, false);

        // Add connexion listener if connexion failed
        mButtonConnexion = mParentView.findViewById(R.id.spotify_connect_btn_connect);
        mButtonConnexion.setOnClickListener(v -> authenticateSpotify());

        // Automatically connect when page loads
        authenticateSpotify();

        msharedPreferences = mContext.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(mContext);

        return mParentView;
    }

    private void waitForUserInfo() {
        SpotifyUserService userService = new SpotifyUserService(queue, msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            editor = mContext.getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);
            // We use commit instead of apply because we
            // need the information stored immediately
            editor.commit();

            // Start new fragment
            NavDirections action = SpotifyConnectFragmentDirections.actionSpotifyConnectFragmentToListMusicFragment("","",SOURCE_SPOTIFY);
            Navigation.findNavController(mParentView).navigate(action);
        });
    }


    private void authenticateSpotify() {
        FragmentActivity activity = getActivity();
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();

        if (null != activity){
            Intent intent = AuthenticationClient.createLoginActivityIntent(activity, request);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = mContext.getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.apply();
                    waitForUserInfo();
                    break;

                // Auth flow returned an error
                case ERROR:
                    resetLayout();

                // Most likely auth flow was cancelled
                default:
                    resetLayout();
            }
        }
    }

    private void resetLayout(){
        TextView result_error = mParentView.findViewById(R.id.spotify_connect_text);
        result_error.setText(mContext.getResources().getText(R.string.spotify_connect_error_login));

        mButtonConnexion.setVisibility(View.VISIBLE);
    }
}
