package com.example.songtolyrics.Controler;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.songtolyrics.R;

public class HistoriqueActivity extends AppCompatActivity {

    Button menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        menuBtn = findViewById(R.id.historique_btn_menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);      // Création nouvelle intent
                startActivity(mainActivity);                                                        // Lancement nouvelle activité
            }
        });
    }
}