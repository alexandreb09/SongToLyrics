package com.example.songtolyrics.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.connectors.SpotifySongService;
import com.example.songtolyrics.model.Music;
import com.example.songtolyrics.model.ResponseOrionSuggestion;
import com.example.songtolyrics.view.MusicAdapter;
import com.example.songtolyrics.view.SimpleDividerItemDecoration;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.songtolyrics.Parameters.DATA_SPOTIFY;
import static com.example.songtolyrics.Parameters.SOURCE_FAVORITE;
import static com.example.songtolyrics.Parameters.SOURCE_LOCAL_STORAGE;
import static com.example.songtolyrics.Parameters.SOURCE_SPOTIFY;
import static com.example.songtolyrics.Parameters.SOURCE_SUGGESTION;
import static com.example.songtolyrics.Parameters.SPOTIFY_USER_NAME;


public class ListMusicFragment extends BaseFragment {
    private MusicAdapter mAdapter;

    private Boolean artistOrder;
    private Boolean songOrder;

    private RetrieveFeedTask mRunningTask;

    private String mTitle;
    private String mArtist;
    private int mSource;
    private List<Music> mListMusics;

    // Required empty public constructor
    public ListMusicFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mArtist = ListMusicFragmentArgs.fromBundle(getArguments()).getArtist();
            mTitle  = ListMusicFragmentArgs.fromBundle(getArguments()).getTitle();
            mSource = ListMusicFragmentArgs.fromBundle(getArguments()).getSource();
        }else{
            mTitle = "";
            mArtist = "";
            mSource = SOURCE_LOCAL_STORAGE;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParentview = inflater.inflate(R.layout.fragment_list_music, container, false);

        TextView upper_txt  = mParentview.findViewById(R.id.list_music_txt_top);
        TextView middle_txt = mParentview.findViewById(R.id.list_music_txt_middle);
        TextView lower_txt  = mParentview.findViewById(R.id.list_music_txt_bottom);

        // RECYCLER VIEW
        RecyclerView mRecyclerView  = mParentview.findViewById(R.id.list_music_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        String toolBarTitle;

        // If the activity source is looking for suggestion
        if (SOURCE_SUGGESTION == mSource){
            toolBarTitle = mContext.getResources().getString(R.string.txt_suggestions);

            // Start loading suggestion task
            mRunningTask = new RetrieveFeedTask(mTitle, mArtist, getActivity());
            mRunningTask.execute();

            // Update layout text
            upper_txt.setText(mContext.getResources().getString(R.string.list_music_suggestion_loading));
            middle_txt.setText(mTitle);
            lower_txt.setText(mArtist);

            // Show progress bar
            ProgressBar progressBar = mParentview.findViewById(R.id.list_music_progressBar);
            progressBar.setVisibility(View.VISIBLE);

            mAdapter = new MusicAdapter(mContext, new ArrayList<>(), mParentview, SOURCE_SUGGESTION);

            // Add listener on order button (title - artist)
            setUpMusicOrderListener(mListMusics, mParentview);
        }
        // If the activity source is looking recently played spotify musics
        else if (SOURCE_SPOTIFY ==  mSource){
            toolBarTitle = mContext.getResources().getString(R.string.txt_spotify);

            // Update layout text
            upper_txt.setText(mContext.getResources().getString(R.string.list_music_txt_advice_spotify));
            middle_txt.setText(mContext.getResources().getString(R.string.list_music_txt_advice_spotify_detail));

            // Show Spotify user
            SharedPreferences msharedPreferences = mContext.getSharedPreferences(DATA_SPOTIFY, 0);
            lower_txt.setVisibility(View.VISIBLE);
            lower_txt.setText(msharedPreferences.getString(SPOTIFY_USER_NAME, ""));

            // Return musics from Spotify service
            SpotifySongService songService = new SpotifySongService(mContext);
            songService.getRecentlyPlayedTracks(() -> {
                mListMusics = new ArrayList<>();
                // Remove duplicates
                for (Music m: songService.getSongs()){
                    if (!mListMusics.contains(m)) mListMusics.add(m);
                }

                mAdapter = new MusicAdapter(mContext, mListMusics, mParentview, SOURCE_SPOTIFY);
                // Populate recycler view
                mRecyclerView.setAdapter(mAdapter);

                // Add listener on order button (title - artist)
                setUpMusicOrderListener(mListMusics, mParentview);
            });
        }
        // If the activity source is FAVORITES
        else if (SOURCE_FAVORITE == mSource){
            toolBarTitle = mContext.getResources().getString(R.string.txt_favorite);

            mListMusics = Utils.readFavoriteFromStorage(mContext);

            // Update layout text
            upper_txt.setText(mContext.getResources().getString(R.string.list_music_txt_advice_spotify));
            middle_txt.setText(mContext.getResources().getString(R.string.txt_favorite));
            lower_txt.setText(mArtist);


            Collections.sort(mListMusics);
            mAdapter = new MusicAdapter(mContext, mListMusics, mParentview, SOURCE_FAVORITE);

            // Populate recycler view
            mRecyclerView.setAdapter(mAdapter);

            // Add listener on order button (title - artist)
            setUpMusicOrderListener(mListMusics, mParentview);
        }
        // DEFAUT CASE: read music from local Storage
        else {
            toolBarTitle = mContext.getResources().getString(R.string.menu_list_musics_phone);

            // Update layout text
            upper_txt.setVisibility(View.INVISIBLE);
            middle_txt.setText(mContext.getResources().getString(R.string.list_music_txt_advice_local));
            lower_txt.setVisibility(View.INVISIBLE);

            // Read music from telephone (artist and title) and update content from history
            mListMusics = Utils.updateLyrics(mContext, Utils.readSong(mContext));
            Collections.sort(mListMusics);
            mAdapter = new MusicAdapter(mContext, mListMusics, mParentview, SOURCE_LOCAL_STORAGE);

            // Populate recycler view
            mRecyclerView.setAdapter(mAdapter);

            // Add listener on order button (title - artist)
            setUpMusicOrderListener(mListMusics, mParentview);
        }

        Utils.setToolbarTitle(getActivity(), toolBarTitle);



        // ===================================== //
        //                BUTTONS                //
        // ===================================== //

        // Return homepage
//        mAccueilBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        });

        // Add listener on order button (title - artist)
        setUpMusicOrderListener(mListMusics, mParentview);

        return mParentview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel running task(s) to avoid memory leaks
        if (mRunningTask != null)
            mRunningTask.cancel(true);
    }

    private void setUpMusicOrderListener(List<Music> songList, View view){
        // Init song - artist order
        songOrder = true;
        artistOrder = false;

        Button mSortSongBtn = view.findViewById(R.id.list_music_music_order_button);
        Button mSortArtistesBtn = view.findViewById(R.id.list_music_artist_order_button);

        // Update sort order from song
        mSortSongBtn.setOnClickListener(v -> {
            Collections.sort(songList);
            if (songOrder){
                Collections.reverse(songList);
            }
            mAdapter.notifyDataSetChanged();
            // Update song - artist order
            songOrder = !songOrder;
            artistOrder = false;

            int drawable = songOrder ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow_upward_black_24dp;
            mSortSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0);
            mSortArtistesBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        });

        // Update sort order from artist
        mSortArtistesBtn.setOnClickListener(v -> {
            // Sort list
            Collections.sort(songList, (m1, m2) -> {
                return m1.getArtist().compareToIgnoreCase(m2.getArtist()); // To compare string values
            });
            // Reverse if asked
            if (artistOrder){
                Collections.reverse(songList);
            }
            mAdapter.notifyDataSetChanged();
            // Update song - artist order
            songOrder = false;
            artistOrder = !artistOrder;

            int drawable = artistOrder ? R.drawable.ic_arrow_downward_black_24dp : R.drawable.ic_arrow_upward_black_24dp;
            mSortArtistesBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0);
            mSortSongBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        });
    }


    static class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        String song;
        String artist;
        private WeakReference<FragmentActivity> activityReference;

        RetrieveFeedTask(String s, String a, FragmentActivity context){
            song = s.replace(" ", "%20");
            artist = a.replace(" ", "%20");
            activityReference = new WeakReference<>(context);
        }

        protected String doInBackground(Void... urls) {
            // Do some validation
            try {
                URL url = new URL(Parameters.ORION_API_URL_SUGGESTION + artist + "%20" + song + "&apikey=" + Parameters.ORION_API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(Parameters.REQUEST_TIMEOUT);
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "";
            }
        }

        protected void onPostExecute(String response_str) {
            // get a reference to the activity if it is still there
            FragmentActivity activity = activityReference.get();

            // Check the current activity is still running
            if (activity != null){
                boolean success = !response_str.isEmpty();

                // Hide progress bar
                activityReference.get().findViewById(R.id.list_music_progressBar).setVisibility(View.GONE);


                View view = activity.findViewById(R.id.fragment_list_music);
                TextView upper_txt = view.findViewById(R.id.list_music_txt_top);

                if (success) {
                    upper_txt.setText(activity.getApplicationContext().getResources().getString(R.string.list_music_txt_advice_suggestion));
                    Gson gson = new Gson();

                    try {
                        JSONObject jsonObject = new JSONObject(response_str);
                        ResponseOrionSuggestion response = gson.fromJson(jsonObject.toString(), ResponseOrionSuggestion.class);

                        if (response.isSuccess() && !response.getMusics().isEmpty()) {
                            List<Music> musics = Utils.updateLyrics(activity, response.getMusics());

                            // Populate recycler view
                            MusicAdapter mAdapter = new MusicAdapter(activity, musics, view, SOURCE_SUGGESTION);
                            RecyclerView mRecyclerView = activityReference.get().findViewById(R.id.list_music_recycler_view);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(activity));
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            success = false;
                        }
                    } catch (JSONException err) {
                        success = false;
                    }
                }

                if (!success){
                    upper_txt.setText(activity.getApplicationContext().getResources().getString(R.string.list_music_suggestion_not_found_title));

                    Context context = activity.getApplicationContext();
                    new FancyAlertDialog.Builder(activity)
                            .setTitle(context.getResources().getString(R.string.list_music_suggestion_not_found_title))
                            .setBackgroundColor(Color.parseColor("#F57C00"))
                            .setMessage(context.getResources().getString(R.string.list_music_suggestion_not_found_msg))
                            .setNegativeBtnText("ANNULER")
                            .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                            .setPositiveBtnText("MODIFIER")
                            .setPositiveBtnBackground(Color.parseColor("#F57C00"))
                            .setAnimation(Animation.POP)
                            .isCancellable(true)
                            .setIcon(R.drawable.ic_pan_tool_black_24dp, Icon.Visible)
                            .OnPositiveClicked(() -> {
                                NavController controller = Navigation.findNavController(view);
                                controller.popBackStack();
                            })
                            .OnNegativeClicked(() -> {})
                            .build();
                }
            }
        }
    }
}
