package com.example.songtolyrics.fragments;

import android.os.Bundle;

import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.songtolyrics.R;
import com.example.songtolyrics.Utils;
import com.example.songtolyrics.model.Music;
import com.google.android.material.snackbar.Snackbar;

public class LyricsResultFragment extends BaseFragment {

    private Music mMusic;
    private boolean mAdd;

    // Required empty public constructor
    public LyricsResultFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMusic = LyricsResultFragmentArgs.fromBundle(getArguments()).getMusic();
        }else {
            mMusic = new Music();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics_result, container, false);

        // Set toolbar title
        String toolBarTitle = mContext.getResources().getString(R.string.label_lyrics_result);
        Utils.setToolbarTitle(getActivity(), toolBarTitle);

        // Find view
        TextView lyrics_view = view.findViewById(R.id.lyrics_result_lyrics);
        TextView title_middle_view = view.findViewById(R.id.lyrics_result_title_middle);
        TextView title_bottom_view = view.findViewById(R.id.lyrics_result_title_bottom);
        Button new_search = view.findViewById(R.id.lyrics_result_new_search);

        // Update view text
        lyrics_view.setText(mMusic.getLyrics());
        title_middle_view.setText(mMusic.getTitle());
        title_bottom_view.setText(mMusic.getArtist());

        // Show a button for a new activity when user went bottom of scrollview
        view.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // View at bottom
            if (!view.canScrollVertically(1)) {
                if (View.VISIBLE != new_search.getVisibility()){
                    // Show button after 1.5s
                    new Handler().postDelayed(() -> new_search.setVisibility(View.VISIBLE), 1500);
                }
            }
        });


        ImageView favorites_img = view.findViewById(R.id.lyrics_result_favorites);
        mAdd = true;
        favorites_img.setOnClickListener((v) -> {
            if (mAdd){
                favorites_img.setImageResource(R.drawable.ic_favorite_black_24dp);

                // Add music to favorites (in memory)
                Utils.addFavoriteAndStore(mContext, mMusic);

                Snackbar.make(v, "Musique ajouté aux favoris",
                        Snackbar.LENGTH_SHORT).show();
            }else{
                // Add music to favorites (in memory)
                Utils.removeFavoriteAndStore(mContext, mMusic);

                Snackbar.make(v, "Musique supprimée des favoris",
                        Snackbar.LENGTH_SHORT).show();
                favorites_img.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
            mAdd = !mAdd;
        });

        new_search.setOnClickListener(v -> {
            LyricsResultFragmentDirections.ActionLyricsResultFragmentToHomeFragment action =
                    LyricsResultFragmentDirections.actionLyricsResultFragmentToHomeFragment("","");
            Navigation.findNavController(view).navigate(action);
        });

        return view;
    }
}
