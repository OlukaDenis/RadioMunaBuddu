package com.mcdenny.radiomunabuddu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.listen_radio)
    FancyButton listenRadio;
    ImageView facebook, twitter, instagram, linkedin;
    RadioSettings settings;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        settings = new RadioSettings();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void openFacebookProfile() {
        try {
            String facebookURL = getFacebookPageUrl();
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(facebookURL));
            startActivity(facebookIntent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getFacebookPageUrl() {
        final String facebookUrl = settings.getFacebookAddress();
        String fbURL = null;
        PackageManager packageManager = getPackageManager();
        try {
            if (packageManager != null){
                Intent fbIntent = packageManager.getLaunchIntentForPackage("com.facebook.katana");
                if (fbIntent != null){
                    int versionCode = packageManager.getPackageInfo("com.facebook.katana",0).versionCode;
                    if (versionCode >= 3002850){
                        fbURL = "fb://page/1993598950880589";
                    }
                } else {
                    fbURL = facebookUrl;
                }
            }
            else {
                fbURL = facebookUrl;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            fbURL = facebookUrl;
        }
        return fbURL;
    }

    private void openTwitterProfile(){
        Intent intent = null;
        try {
            this.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=USERID"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/profilename"));
        }
        this.startActivity(intent);
    }

    //click listener in Listen now button
    @OnClick(R.id.listen_radio)
    void listenRadio() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.watch_webcam: {
                launchWebcam();
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT  , "https://play.google.com");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "https://radiomunnabudduusa.com");
            startActivity(Intent.createChooser(shareIntent, "Share via..."));

        }
        else if (id == R.id.nav_email){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: "+settings.getEmailAddress()));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Radio Munnabuddu");
            if (emailIntent.resolveActivity(getPackageManager()) != null){
                startActivity(Intent.createChooser(emailIntent, "Send email via"));
            }
        }
        else if(id == R.id.nav_report){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: denis@premar.tech"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Crash or Bug report");
             if (emailIntent.resolveActivity(getPackageManager()) != null){
                 startActivity(Intent.createChooser(emailIntent, "Send email via."));
             }
         }
        else if(id == R.id.nav_about){
            Intent aboutIntent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(aboutIntent);
        }
        else if(id == R.id.nav_fb){
            openFacebookProfile();
        }
        else if(id == R.id.nav_twitter){
          openTwitterProfile();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    /**
     * Launches webcam from external URL
     */
    public void launchWebcam(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.getRadioWebcamURL()));
        startActivity (browserIntent);
    }



}
