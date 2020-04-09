package com.example.songtolyrics.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.songtolyrics.Parameters;
import com.example.songtolyrics.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import static com.example.songtolyrics.Parameters.SOURCE_FAVORITE;
import static com.example.songtolyrics.Parameters.SOURCE_LOCAL_STORAGE;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AccueilFragment extends Fragment {
    private Context mContext;
    private View mParentview;

    public AccueilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParentview = inflater.inflate(R.layout.fragment_accueil, container, false);

        Button btn_reccord              = mParentview.findViewById(R.id.homepage_btn_record);
        Button btn_favoris              = mParentview.findViewById(R.id.homepage_btn_favoris);
        Button btn_listMusique          = mParentview.findViewById(R.id.homepage_btn_show_musics);
        Button btn_lyrics               = mParentview.findViewById(R.id.homepage_btn_search_lyrics);
        Button btn_spotify              = mParentview.findViewById(R.id.homepage_btn_spotify_connect);

        btn_reccord.setOnClickListener(v -> {
            // Ask permission:
            // Permission is automatically granted bellow sdk 16
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                // Ask permission to the user
                requestPermissions(
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        Parameters.REQUEST_RECORD_AUDIO_PERMISSION
                );
            }else {
                startReccordFragment(mParentview);
            }
        });

        btn_listMusique.setOnClickListener(v -> {
            // Ask permission:
            // Permission is automatically granted bellow sdk 16
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Parameters.REQUEST_MEMORY_PERMISSION
                );
            }else {
                startListMusicFragment(mParentview);
            }
        });

        btn_favoris.setOnClickListener(v -> {
            AccueilFragmentDirections.ActionAccueilFragmentToListMusicFragment action =
                AccueilFragmentDirections.actionAccueilFragmentToListMusicFragment("","", SOURCE_FAVORITE);
            Navigation.findNavController(mParentview).navigate(action);
        });

        btn_lyrics.setOnClickListener(v -> startLyricsFragment(mParentview));

        btn_spotify.setOnClickListener(v -> {
            NavDirections action = AccueilFragmentDirections.actionAccueilFragmentToSpotifyConnectFragment();
            Navigation.findNavController(mParentview).navigate(action);
        });

        return mParentview;
    }

    /**
     * Gérer les demandes d'autorisatons si demande accès à la mémoire (lecture musiques)
     * Paramètre : resquestCode (int), permissions[] (String), grantResults (int[])
     * Type : void
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==  Parameters.REQUEST_MEMORY_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListMusicFragment(mParentview);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(mParentview, R.string.autorisation_acces_memoire_refuse, Snackbar.LENGTH_LONG)
                            .setAction("Paramètres", view -> {
                                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                final Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }).show();
                }
                else {
                    Snackbar.make(mParentview,R.string.autorisation_acces_memoire_refuse, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }else if(requestCode ==  Parameters.REQUEST_RECORD_AUDIO_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startReccordFragment(mParentview);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)){
                    Snackbar.make(mParentview, R.string.autorisation_acces_memoire_refuse, Snackbar.LENGTH_LONG)
                            .setAction("Paramètres", view -> {
                                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                final Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }).show();
                }
                else {
                    Snackbar.make(mParentview, R.string.autorisation_acces_micro_refuse, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    /**
     * Start Lyrics activity
     * @param view : fragment view
     */
    private void startLyricsFragment(View view){
        AccueilFragmentDirections.ActionAccueilFragmentToLyricsFragment action =
                AccueilFragmentDirections.actionAccueilFragmentToLyricsFragment("", "");
        Navigation.findNavController(view).navigate(action);
    }

    /**
     * Start the List music fragment
     * @param view : fragment parent view
     */
    private void startListMusicFragment(View view){
        AccueilFragmentDirections.ActionAccueilFragmentToListMusicFragment action =
                AccueilFragmentDirections.actionAccueilFragmentToListMusicFragment("", "", SOURCE_LOCAL_STORAGE);
        Navigation.findNavController(view).navigate(action);
    }

    private void startReccordFragment(View view){
        NavDirections action = AccueilFragmentDirections.actionAccueilFragmentToReccordFragment();
        Navigation.findNavController(view).navigate(action);
    }
}
