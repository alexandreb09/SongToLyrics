package com.example.songtolyrics.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.connectors.SpotifyUserService;
import com.example.songtolyrics.model.User;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


public class SpotifyConnectFragment extends BaseFragment {
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private View mParentView;
    private Button mButtonConnexion;

    private RequestQueue queue;


    // Required empty public constructor
    public SpotifyConnectFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParentView = inflater.inflate(R.layout.fragment_spotify_connect, container, false);

        // Set toolbar title
        String toolBarTitle = mContext.getResources().getString(R.string.txt_spotify);
        Utils.setToolbarTitle(getActivity(), toolBarTitle);

        // Add connexion listener if connexion failed
        mButtonConnexion    = mParentView.findViewById(R.id.spotify_connect_btn_connect);
        Button buttonExit   = mParentView.findViewById(R.id.spotify_connect_leave);
        mButtonConnexion.setOnClickListener(v -> authenticateSpotify());
        buttonExit.setOnClickListener(v -> Navigation.findNavController(mParentView).popBackStack());

        // Automatically connect when page loads
        authenticateSpotify();

        msharedPreferences = mContext.getSharedPreferences(Parameters.DATA_SPOTIFY, 0);
        queue = Volley.newRequestQueue(mContext);

        return mParentView;
    }

    private void waitForUserInfo() {
        SpotifyUserService userService = new SpotifyUserService(queue, msharedPreferences);
        userService.get(() -> {
            User user = userService.getUser();
            editor = mContext.getSharedPreferences(Parameters.DATA_SPOTIFY, 0).edit();
            editor.putString("userid", user.id);
            editor.putString(Parameters.SPOTIFY_USER_NAME, user.display_name);
            // We use commit instead of apply because we
            // need the information stored immediately
            editor.commit();

            // Start new fragment
            NavDirections action = SpotifyConnectFragmentDirections.actionSpotifyConnectFragmentToListMusicFragment("","",Parameters.SOURCE_SPOTIFY);
            Navigation.findNavController(mParentView).navigate(action);
        });
    }


    private void authenticateSpotify() {
        ProgressBar progressBar = mParentView.findViewById(R.id.spotify_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        TextView result_error = mParentView.findViewById(R.id.spotify_connect_text);
        result_error.setText(mContext.getResources().getText(R.string.spotify_connect_connection));

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(Parameters.SPOTIFY_CLIENT_ID,
                        AuthenticationResponse.Type.TOKEN,
                        Parameters.SPOTIFY_REDIRECT_URI);

        builder.setScopes(new String[]{Parameters.SPOTIFY_SCOPES});
        AuthenticationRequest request = builder.build();

        FragmentActivity activity = getActivity();

        if (null != activity ){
            Intent intent = AuthenticationClient.createLoginActivityIntent(activity, request);
            startActivityForResult(intent, Parameters.SPOTIFY_REQUEST_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == Parameters.SPOTIFY_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    editor = mContext.getSharedPreferences(Parameters.DATA_SPOTIFY, 0).edit();
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

                    new FancyAlertDialog.Builder(getActivity())
                            .setTitle(mContext.getResources().getString(R.string.spotify_connect_error_login_title))
                            .setBackgroundColor(Color.parseColor("#F57C00"))
                            .setMessage(mContext.getResources().getString(R.string.spotify_connect_error_login_message))
                            .setNegativeBtnText("Annuler")
                            .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                            .setPositiveBtnBackground(Color.parseColor("#F57C00"))
                            .setPositiveBtnText("Réessayer !")
                            .isCancellable(true)
                            .setAnimation(Animation.POP)
                            .setIcon(R.drawable.ic_pan_tool_black_24dp, Icon.Visible)
                            .OnPositiveClicked(this::authenticateSpotify)
                            .OnNegativeClicked(() -> {})
                            .build();

            }
        }
    }

    private void resetLayout(){
        TextView result_error = mParentView.findViewById(R.id.spotify_connect_text);
        result_error.setText(mContext.getResources().getText(R.string.spotify_connect_error_login));

        ProgressBar progressBar = mParentView.findViewById(R.id.spotify_progress_bar);
        progressBar.setVisibility(View.GONE);

        mButtonConnexion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
