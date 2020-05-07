package com.premar.radiomunabuddu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.premar.radiomunabuddu.AppUtils.ALL_PERMISSIONS;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FancyButton listenRadio;
    ImageView facebook, twitter, instagram, linkedin;
    RadioSettings settings;
    Context context;
    private static final String TAG = "HomeActivity";
    public static final int REQUEST_CODE =123;

    private Button stopButton = null;
    private Button playButton = null;
    private Button phoneCall, EmailPress, WWWPress, TxtPress;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        settings = new RadioSettings();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        checkAndRequestPermissions();

        //views
        phoneCall = findViewById(R.id.phoneBtn);
        WWWPress = findViewById(R.id.websiteBtn);
        TxtPress = findViewById(R.id.txtBtn);
        EmailPress = findViewById(R.id.emailBtn);


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
        playButton = findViewById(R.id.PlayButton);
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),
                    RadioMediaPlayerService.class);
            intent.putExtra(RadioMediaPlayerService.START_PLAY, true);
            startService(intent);

        });

        //Stop button
        stopButton = findViewById(R.id.StopButton);
        stopButton.setOnClickListener(v -> {
            //Get new MediaPlayerService activity
            Intent intent = new Intent(getApplicationContext(),
                    RadioMediaPlayerService.class);
            stopService(intent);
        });

        //Email Button click list

        EmailPress.setOnClickListener(view -> {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: "+settings.getEmailAddress()));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Radio MB FM");
            if (emailIntent.resolveActivity(getPackageManager()) != null){
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HomeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Website Button
        WWWPress.setOnClickListener(view -> {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.getWebsiteURL())); //URL
            startActivity (browserIntent);

        });

        TxtPress.setOnClickListener(view -> {
            sendSms();
        });

        //Call phone
        phoneCall.setOnClickListener(view -> onCall() );

    }


    /**
     * Launches webcam from external URL
     */
    public void launchWebcam(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settings.getRadioWebcamURL()));
        startActivity (browserIntent);
    }


    public void sendSms() {
        if (checkAndRequestPermissions()) {
            if (AppUtils.getDefaultSmsAppPackageName(this) != null) {
//                Uri uri = Uri.parse("sms_to: " + settings.getSmsNumber());
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                intent.setDataAndType(uri,"vnd.android-dir/mms-sms");
//                intent.putExtra("sms_body", "Hello Presenter,");
//                this.startActivity(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
                {
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getApplicationContext()); //Need to change the build to API 19

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello Presenter");

                    if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                    {
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    this.startActivity(sendIntent);

                }
                else //For early versions, do what worked for you before.
                {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                    sendIntent.setData(Uri.parse("sms_to:"+ settings.getSmsNumber()));
                    sendIntent.setDataAndType(Uri.parse("sms_to:"+ settings.getSmsNumber()),
                            "vnd.android-dir/mms-sms");
                    sendIntent.putExtra("sms_body", "Hello Presenter");
                    this.startActivity(sendIntent);
                }
            }
        }
    }

    public void onCall() {
        if (checkAndRequestPermissions()) {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + settings.getPhoneNumber())));
        }
    }



    private  boolean checkAndRequestPermissions() {
        int permissionSms = ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS);
        int permissionCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (permissionCall != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ALL_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        if (requestCode == ALL_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions
            perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);

            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED ){
                    Log.d(TAG, "Call & SMS services permission granted");
                    // process the normal flow

                    //else any one or both the permissions are not granted
                } else {
                    Log.d(TAG, "Some permissions are not granted ask again ");
                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        showDialogOK("Service Permissions are required for this app",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkAndRequestPermissions();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish();
                                            break;
                                    }
                                });
                    }
                    //permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                        //proceed with logic by disabling the related features or quit the app.
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
    private void explain(String msg){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", (paramDialogInterface, paramInt) -> {
                    //  permissionsclass.requestPermission(type,code);
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:com.app.ugaid")));
                })
                .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> finish());
        dialog.show();
    }
}
