package com.example.songtolyrics.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.ResponseOrionLyrics;
import com.google.gson.Gson;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.songtolyrics.Parameters.SOURCE_SUGGESTION;


public class HomeFragment extends BaseFragment {
    private String mTitle;
    private String mArtist;

    private EditText mSongName;
    private EditText mArtistName;
    private ProgressBar mProgressBar;

    private RetrieveFeedTask mRunningTask;
    private List<Music> mMusicHistory;

    // Required empty public constructor
    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mTitle = HomeFragmentArgs.fromBundle(getArguments()).getTitle();
            mArtist = HomeFragmentArgs.fromBundle(getArguments()).getArtist();
        }else{
            mTitle = "";
            mArtist = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParentView = inflater.inflate(R.layout.fragment_home, container, false);

        String toolBarTitle = mContext.getResources().getString(R.string.menu_search);
        Utils.setToolbarTitle(getActivity(), toolBarTitle);

        mSongName       = mParentView.findViewById(R.id.songName);
        mArtistName     = mParentView.findViewById(R.id.artistName);
        mProgressBar    = mParentView.findViewById(R.id.progressBar);

        Button mQueryButton = mParentView.findViewById(R.id.queryButton);


        if (!mArtist.isEmpty()) mArtistName.setText(mArtist);
        if (!mTitle.isEmpty()) mSongName.setText(mTitle);

        // Load previous searched
        mMusicHistory =  Utils.restoreMusicHistory(mContext);

        mQueryButton.setOnClickListener(v -> {
            String song = mSongName.getText().toString();
            String artist = mArtistName.getText().toString();

            // Check song field not empty
            if (TextUtils.isEmpty(song)){
                mSongName.setError(getResources().getString(R.string.search_lyrics_error_title_missing));
            }
            // Check artist field not empty
            else if (TextUtils.isEmpty(artist)){
                mArtistName.setError(getResources().getString(R.string.search_lyrics_error_author_missing));
            }
            // Check song+artist not already search without success
            else if (Utils.doResearch(artist, song, mMusicHistory)){
                mProgressBar.setVisibility(View.VISIBLE);
                mQueryButton.setEnabled(false);

                mRunningTask = new RetrieveFeedTask(song, artist, mMusicHistory, getActivity());
                mRunningTask.execute();
            }
            // A similar research have already been done with no success
            //      -> directly redirect to not found activity
            else{
                showNotFoundDialog(getActivity(), mContext, mParentView, song, artist);
                mQueryButton.setEnabled(true);
            }
        });

        return mParentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel running task(s) to avoid memory leaks
        if (mRunningTask != null)
            mRunningTask.cancel(true);
    }

    /**
     * Start not found activity
     * @param fragmentActivity: fragment activity
     * @param context: fragment context
     * @param title: song title not found
     * @param artist: artist not found
     */
    private static void showNotFoundDialog(FragmentActivity fragmentActivity,
                                              Context context,
                                              View view,
                                              String title,
                                              String artist){
        new FancyAlertDialog.Builder(fragmentActivity)
                .setTitle(context.getResources().getString(R.string.not_found_title))
                .setBackgroundColor(Color.parseColor("#F57C00"))
                .setMessage(context.getResources().getString(R.string.not_found_message))
                 .setNegativeBtnText("MODIFIER")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                .setPositiveBtnBackground(Color.parseColor("#F57C00"))
                .setPositiveBtnText(context.getResources().getString(R.string.not_found_suggestions))
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_pan_tool_black_24dp, Icon.Visible)
                .OnPositiveClicked(() -> {
                    HomeFragmentDirections.ActionHomeFragmentToListMusicFragment action =
                            HomeFragmentDirections.actionHomeFragmentToListMusicFragment(title, artist, SOURCE_SUGGESTION);
                    Navigation.findNavController(view).navigate(action);
                })
                .OnNegativeClicked(() -> {
                    Button button = view.findViewById(R.id.queryButton);
                    button.setEnabled(true);
                })
                .build();
    }


    static class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        String song;
        String artist;
        List<Music> musicHistory;
        private WeakReference<FragmentActivity> activityReference;

        RetrieveFeedTask(String s, String a, List<Music> musicHistory_, FragmentActivity context){
            this.song = s;
            this.artist = a;
            this.musicHistory = musicHistory_;
            this.activityReference = new WeakReference<>(context);
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            String response = "{\"result\": {\"error\": \"Lyric no found, try again later.\"}}";

            // Do some validation
            try {
                // Build URL
                URL url = new URL(Parameters.ORION_API_URL_LYRICS + artist + "/" + song + "?" +"apikey=" + Parameters.ORION_API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    if (0 < stringBuilder.length()){
                        response = stringBuilder.toString();
                    }
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
            return response;
        }

        protected void onPostExecute(String response) {
            // get a reference to the activity if it is still there
            FragmentActivity activity = activityReference.get();

            // Check the current activity is still running
            if (!(activity == null || activity.isFinishing())){

                // Hide progress bar
                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                // Unserialize response
                ResponseOrionLyrics responseOrion;
                try {
                    // Get result from request
                    JSONObject jsonObject = new JSONObject(response).optJSONObject("result");
                    // Unserialized result to a ResponseOrionLyrics object
                    responseOrion = new Gson().fromJson(jsonObject.toString(), ResponseOrionLyrics.class);
                }catch (JSONException err){
                    Log.d("Error", err.toString());
                    responseOrion = new ResponseOrionLyrics("Aucune music disponible");
                }

                View view = activity.findViewById(R.id.fragment_lyrics);

                // If valid response
                if (responseOrion.isValid()){
                    // Add music to history and store it
                    Music music = new Music(responseOrion);
                    musicHistory = Utils.addMusic(activity, musicHistory, music);
                    Utils.storeMusicHistory(activity, this.musicHistory);

                    // Show results
                    HomeFragmentDirections.ActionHomeFragmentToLyricsResultFragment action =
                            HomeFragmentDirections.actionHomeFragmentToLyricsResultFragment(music);
                    Navigation.findNavController(view).navigate(action);
                }
                // If invalid response
                else{
                    Music music = new Music(song, artist);

                    // Add music to history and store it
                    musicHistory = Utils.addMusic(activity, musicHistory, music);
                    Utils.storeMusicHistory(activity, this.musicHistory);

                    // Start not found activity
                    HomeFragment.showNotFoundDialog(activity, activity.getApplicationContext(),view, song, artist);
                }
            }
        }
    }
}
