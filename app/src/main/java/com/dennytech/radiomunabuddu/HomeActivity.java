package com.dennytech.radiomunabuddu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.dennytech.radiomunabuddu.exo_player.AppCons;
import com.dennytech.radiomunabuddu.exo_player.RadioManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dennytech.radiomunabuddu.AppUtils.ALL_PERMISSIONS;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.playPauseBtn)
    ImageButton playPauseBtn;

    private Button phoneCall, EmailPress, WWWPress, TxtPress;
    private FirebaseAnalytics mFirebaseAnalytics;
    RadioManager radioManager;

    private String streamURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        streamURL = AppCons.radioStreamURL;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        checkAndRequestPermissions();
        AppUtils.isNetworkAvailable(this);

        radioManager = RadioManager.with(this);

        //views
        phoneCall = findViewById(R.id.phoneBtn);
        WWWPress = findViewById(R.id.websiteBtn);
        TxtPress = findViewById(R.id.txtBtn);
        EmailPress = findViewById(R.id.emailBtn);


        //Allow hardware audio buttons to control volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        clickListeners(); //Start click listeners

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @OnClick(R.id.playPauseBtn)
    public void onClicked(){

        if(TextUtils.isEmpty(streamURL)) return;

        radioManager.playOrPause(streamURL);
    }

    @Subscribe
    public void onEvent(String status){

        switch (status){

            case AppCons.LOADING:

                // loading

                break;

            case AppCons.ERROR:

                Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();

                break;

        }

        playPauseBtn.setBackgroundResource(status.equals(AppCons.PLAYING)
                ? R.drawable.ic_pause
                : R.drawable.ic_play);

    }


    /**
     * Listens for contact button clicks
     */
    private void clickListeners(){
        EmailPress.setOnClickListener(view -> {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: "+AppCons.emailAddress));
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

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppCons.websiteURL)); //URL
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppCons.radioWebcamURL));
        startActivity (browserIntent);
    }


    public void sendSms() {
        if (whatsappInstalledOrNot("com.whatsapp")) {
            Uri uri = Uri.parse("smsto:" + AppCons.smsNumber);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(i, ""));
        }  else {
            Toast.makeText(this, "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
        }

    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void onCall() {
        if (checkAndRequestPermissions()) {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + AppCons.phoneNumber)));
        }
    }



    private  boolean checkAndRequestPermissions() {
        int permissionCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);


        List<String> listPermissionsNeeded = new ArrayList<>();

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

            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Call services permission granted");
                    // process the normal flow

                    //else any one or both the permissions are not granted
                } else {
                    Log.d(TAG, "Some permissions are not granted ask again ");
                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
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

    @Override
    public void onStart() {

        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {

        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        radioManager.unbind();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        radioManager.bind();
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
        final String facebookUrl = AppCons.facebookAddress;
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
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Radio MB FM");
            startActivity(Intent.createChooser(shareIntent, "Share via..."));

        }
        else if (id == R.id.nav_email){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: "+ AppCons.emailAddress));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Radio MB FM");
            if (emailIntent.resolveActivity(getPackageManager()) != null){
                startActivity(Intent.createChooser(emailIntent, "Send email via"));
            }
        }
        else if(id == R.id.nav_report){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: dennytech5@gmail.com"));
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
}
