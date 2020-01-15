package com.example.songtolyrics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_nouvelle_recherche;
    Button btn_historique;
    Button btn_listMusique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_nouvelle_recherche = findViewById(R.id.homepage_btn_record);
        btn_historique = findViewById(R.id.homepage_btn_historique);
        btn_listMusique = findViewById(R.id.homepage_btn_show_musics);


        btn_nouvelle_recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nouvelle_recherche = new Intent(getApplicationContext(), EnregistrementActivity.class); // Création nouvelle intent
                startActivity(nouvelle_recherche);                                                      // Lancement nouvelle activité
            }
        });

        btn_listMusique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nouvelle_recherche = new Intent(getApplicationContext(), ListMusicActivity.class); // Création nouvelle intent
                startActivity(nouvelle_recherche);                                                      // Lancement nouvelle activité
            }
        });

        btn_historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historique = new Intent(getApplicationContext(), HistoriqueActivity.class); // Création nouvelle intent
                startActivity(historique);                                                      // Lancement nouvelle activité
            }
        });
    }
}
