package com.example.songtolyrics.controler;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.example.songtolyrics.Parameters;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import com.example.songtolyrics.R;
import com.google.android.material.snackbar.Snackbar;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.Icon;


public class MainActivity extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;
    private String mHomeLabel;
    private String mMusicsLocalLabel;
    private String mFavoritesLabel;
    private String mRecordLabel;

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHomeLabel              = getApplicationContext().getResources().getString(R.string.menu_search);
        mMusicsLocalLabel       = getApplicationContext().getResources().getString(R.string.menu_list_musics_phone);
        mFavoritesLabel         = getApplicationContext().getResources().getString(R.string.menu_list_musics_favoris);
        mRecordLabel            = getApplicationContext().getResources().getString(R.string.menu_record);

        // Find elements
        mDrawerLayout           = findViewById(R.id.homepage_drawer_parent);
        Toolbar mToolbar        = findViewById(R.id.activity_main_toolbar);
        NavigationView navView  = findViewById(R.id.activity_main_nav_view);

        // Set up ToolBar
        setSupportActionBar(mToolbar);

        // Set up nav controller + graph + default args
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mNavController.setGraph(R.navigation.nav_graph, createEmptyArtistMusicBundle());
        NavigationUI.setupWithNavController(navView, mNavController);

        // Change default navigation process (for menu item)
        navView.setNavigationItemSelectedListener(this::catchNavigation);

        // Configure Drawer Layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.txt_open, R.string.txt_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    /* ====================================*/
    /* Close Drawer if opened              */
    /* ====================================*/
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /* ====================================*/
    /*              NAVIGATION             */
    /* ====================================*/
    /**
     * Navigate to the ListMusic activity (for a given source)
     * @param source: specify which source is used
     */
    public void navigateToListMusic(int source){
        Bundle b = createEmptyArtistMusicBundle();
        b.putInt("Source", source);
        mNavController.navigate(R.id.listMusicFragment, b);
    }

    /**
     * Navigate to the Recoord activity
     */
    public void navigateToRecord(){
        mNavController.navigate(R.id.reccordFragment);
    }

    public boolean catchNavigation(MenuItem menuItem){
        // Hide drawer bar
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        menuItem.setChecked(true);

        String itemTitle = menuItem.getTitle().toString();
        // Local Musics
        if (itemTitle.equals(mHomeLabel)){
            // This is for maintaining the behavior of the Navigation view
            mNavController.popBackStack(R.id.HomeFragment, false);
        }
        else if (itemTitle.equals(mMusicsLocalLabel)){
            if (permissionReadStorageGranted()){
                navigateToListMusic(Parameters.SOURCE_LOCAL_STORAGE);
            }else{
                // Wait for permission result
                return false;
            }
        }
        // FAVORITES
        else if (itemTitle.equals(mFavoritesLabel)) {
            navigateToListMusic(Parameters.SOURCE_FAVORITE);
        }
        // RECCORD
        else if (itemTitle.equals(mRecordLabel)){
            if (permissionRecordGranted()){
                navigateToRecord();
            }else{
                return false;
            }
        }
        else{
            // This is for maintaining the behavior of the Navigation view
            NavigationUI.onNavDestinationSelected(menuItem, mNavController);
        }

        return true;
    }


    //===========================================//
    //              PERMISSIONS                  //
    //===========================================//
    public boolean permissionRecordGranted(){
        boolean granted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (!granted) {
            // Ask permission to the user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        Parameters.REQUEST_RECORD_AUDIO_PERMISSION
                );
            }else{
                Resources res = getApplicationContext().getResources();
                showPermissionDeniedDialog(this,
                        res.getString(R.string.autorisation_acces_memoire_refuse),
                        res.getString(R.string.autorisation_acces_memoire_refuse_detail));
            }
        }
        return granted;
    }

    public boolean permissionReadStorageGranted(){
        boolean granted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (!granted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Parameters.REQUEST_MEMORY_PERMISSION
                );
            }else{
                Resources res = getApplicationContext().getResources();
                showPermissionDeniedDialog(this,
                        res.getString(R.string.autorisation_acces_memoire_refuse),
                        res.getString(R.string.autorisation_acces_memoire_refuse_detail));
            }
        }
        return granted;
    }

    /**
     * Handle Permissions requests
     * @param requestCode: request ID
     * @param permissions: permissions asked
     * @param grantResults: grant result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        View parentView = findViewById(R.id.nav_host_fragment);
        if (requestCode ==  Parameters.REQUEST_MEMORY_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToListMusic(Parameters.SOURCE_LOCAL_STORAGE);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(parentView, R.string.autorisation_acces_memoire_refuse, Snackbar.LENGTH_LONG)
                            .setAction("Paramètres", view -> openParameters())
                            .show();
                }
                else {
                    Snackbar.make(parentView,R.string.autorisation_acces_memoire_refuse_detail, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }else if(requestCode ==  Parameters.REQUEST_RECORD_AUDIO_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToRecord();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)){
                    Snackbar.make(parentView, R.string.autorisation_acces_micro_refuse, Snackbar.LENGTH_LONG)
                            .setAction("Paramètres", view -> openParameters())
                            .show();
                }
                else {
                    Snackbar.make(parentView, R.string.autorisation_acces_micro_refuse_detail, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    //===========================================//
    //                  OTHERS                   //
    //===========================================//
    public void openParameters(){
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        final Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void showPermissionDeniedDialog(Activity activity, String title, String message){
        new FancyAlertDialog.Builder(activity)
            .setTitle(title)
            .setBackgroundColor(Color.parseColor("#F57C00"))
            .setMessage(message)
            .setNegativeBtnText("Annuler")
            .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
            .setPositiveBtnBackground(Color.parseColor("#F57C00"))
            .setPositiveBtnText("Modifier")
            .setAnimation(Animation.POP)
            .isCancellable(true)
            .setIcon(R.drawable.ic_pan_tool_black_24dp, Icon.Visible)
            .OnPositiveClicked(this::openParameters)
            .OnNegativeClicked(() -> {})
            .build();
    }

    private Bundle createEmptyArtistMusicBundle(){
        Bundle b = new Bundle();
        String ARTIST_KEY = "Artist";
        b.putString(ARTIST_KEY, "");
        String TITLE_KEY = "Title";
        b.putString(TITLE_KEY, "");
        return b;
    }
}
