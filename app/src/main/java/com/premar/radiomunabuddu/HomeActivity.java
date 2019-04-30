package com.premar.radiomunabuddu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FancyButton listenRadio;
    ImageView facebook, twitter, instagram, linkedin;
    RadioSettings settings;
    Context context;
    public static final int REQUEST_CODE =123;

    private Button stopButton = null;
    private Button playButton = null;
    private Button phoneCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        settings = new RadioSettings();

        //views
        phoneCall = (Button)this.findViewById(R.id.phoneBtn);


        //Allow hardware audio buttons to control volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        clickListeners(); //Start click listeners

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

            case R.id.playstore_share: {
                /*
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
                break;
                */
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
             String shareMessage= "\nPlease download our Radiomunnabuddu USA app from the Play Store\n\n";
             shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT  , shareMessage);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Radio Munnabuddu USA");
            startActivity(Intent.createChooser(shareIntent, "Share via..."));

        }
        else if (id == R.id.nav_email){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: "+settings.getEmailAddress()));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Radio Munnabuddu USA");
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
     * Listens for contact button clicks
     */
    private void clickListeners(){
        //Play button
        playButton = (Button)findViewById(R.id.PlayButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        RadioMediaPlayerService.class);
                intent.putExtra(RadioMediaPlayerService.START_PLAY, true);
                startService(intent);

            }
        });

        //Stop button
        stopButton = (Button)findViewById(R.id.StopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Get new MediaPlayerService activity
                Intent intent = new Intent(getApplicationContext(),
                        RadioMediaPlayerService.class);
                stopService(intent);
            }
        });

        //Email Button click list
        final View EmailPress = (Button)this.findViewById(R.id.emailBtn);
        EmailPress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: "+settings.getEmailAddress()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Radio Munnabuddu");
                if (emailIntent.resolveActivity(getPackageManager()) != null){
                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(HomeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        //Website Button
        final View WWWPress = (Button)this.findViewById(R.id.websiteBtn);
        WWWPress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.getWebsiteURL())); //URL
                startActivity (browserIntent);

            }
        });

        //SMS Button
        final View TxtPress = (Button)this.findViewById(R.id.txtBtn);
        TxtPress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                Uri uri = Uri.parse(settings.getSmsNumber());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "Hello Presenter,");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                /*
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(HomeActivity.this, "Please grant the permission to call", Toast.LENGTH_SHORT).show();
                    requestSMSPermission();
                }
                else {
                    startActivity (smsIntent);
                }*/

            }
        });
    }


    /**
     * Launches webcam from external URL
     */
    public void launchWebcam(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.getRadioWebcamURL()));
        startActivity (browserIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall();
                } else {
                    Log.d("TAG", "Call Permission Not Granted");
                    //Toast.makeText(this, "Call Permission Not Granted", Toast.LENGTH_SHORT).show();
                }
                return;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE);
        } else {
            phoneCall.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view){
                    /*
                    String phoneNum = settings.getPhoneNumber();
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:"+ phoneNum));
                    if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(phoneIntent);
                    }
                    */
                    startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + settings.getPhoneNumber())));
                }
            });
            //TODO: put an cation here
        }
    }

    private void requestCallPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CALL_PHONE
        }, 1);
    }

    private void requestSMSPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS
        }, 1);
    }



}
