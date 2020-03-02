package com.example.songtolyrics.controler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.songtolyrics.R;


public class MainActivity extends AppCompatActivity {

    public static final int DEMANDE_AUTORISATION_CODE = 50;

    Button btn_nouvelle_recherche;
    Button btn_historique;
    Button btn_listMusique;
    Button btn_lyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_nouvelle_recherche  = findViewById(R.id.homepage_btn_record);
        btn_historique          = findViewById(R.id.homepage_btn_historique);
        btn_listMusique         = findViewById(R.id.homepage_btn_show_musics);
        btn_lyrics              = findViewById(R.id.homepage_btn_search_lyrics);


        btn_nouvelle_recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nouvelle_recherche = new Intent(getApplicationContext(), ReccordActivity.class); // Création nouvelle intent
                startActivity(nouvelle_recherche);                                                      // Lancement nouvelle activité
            }
        });

        btn_listMusique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ask permission if Permission isn't granted
                // Permission is automatically granted bellow sdk 16
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                            DEMANDE_AUTORISATION_CODE
                    );
                }else {
                    runListMusicActivity();
                }
            }
        });

        btn_historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historique = new Intent(getApplicationContext(), HistoricActivity.class);  // Création nouvelle intent
                startActivity(historique);                                                          // Lancement nouvelle activité
            }
        });

        btn_lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lyrics = new Intent(getApplicationContext(), LyricsActivity.class);          // Création nouvelle intent
                startActivity(lyrics);                                                              // Lancement nouvelle activité
            }
        });
    }

    /**
     * Gérer les demandes d'autorisatons si demande accès à la mémoire (lecture musiques)
     * Paramètre : resquestCode (int), permissions[] (String), grantResults (int[])
     * Type : void
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==  DEMANDE_AUTORISATION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runListMusicActivity();
            } else {
                ConstraintLayout parentLayout = findViewById(R.id.homepage_parent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Snackbar.make(parentLayout, R.string.autorisation_acces_memoire_refuse, Snackbar.LENGTH_LONG)
                        .setAction("Paramètres", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                final Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).show();
                }
                else {
                    Snackbar.make(parentLayout,R.string.autorisation_acces_memoire_refuse, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    public void runListMusicActivity(){
        Intent nouvelle_recherche = new Intent(getApplicationContext(), ListMusicActivity.class);   // Create newintent
        startActivity(nouvelle_recherche);                                                          // Run new activity
    }
}
