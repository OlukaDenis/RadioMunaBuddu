package com.mcdenny.radiomunabuddu;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class RadioMediaPlayerService extends Service {
    //Variables
    private boolean isPlaying = false;
    private MediaPlayer radioPlayer; //The media player instance
    private static int classID = 579; // just a number
    public static String START_PLAY = "START_PLAY";

    //Settings
    RadioSettings settings = new RadioSettings();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Starts the streaming service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(START_PLAY, false)) {
            play();
        }
        return Service.START_STICKY;
    }


    /**
     * Starts radio URL stream
     */
    @SuppressLint("NewApi")
    private void play() {

        //Check connectivity status
        if (isOnline()) {
            //Check if player already streaming
            if (!isPlaying) {
                isPlaying = true;

                //Return to the current activity
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

                //Build and show notification for radio playing
                Notification notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(settings.getRadioName())
                        .setContentText(settings.getMainNotificationMessage())
                        .setSmallIcon(R.drawable.buddu3)
                        .setContentIntent(pi)
                        .build();

                //Get stream URL
                radioPlayer = new MediaPlayer();
                try {
                    radioPlayer.setDataSource(settings.getRadioStreamURL()); //Place URL here
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (settings.getAllowConsole()){
                    //Buffering Info
                    radioPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        public void onBufferingUpdate(MediaPlayer mp, int percent) {
                            Log.i("Buffering", "" + percent);
                        }
                    });
                }

                radioPlayer.prepareAsync();
                radioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    public void onPrepared(MediaPlayer mp) {
                        radioPlayer.start(); //Start radio stream
                    }
                });

                startForeground(classID, notification);

                //Display toast notification
                Toast.makeText(getApplicationContext(), settings.getPlayNotificationMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            //Display no connectivity warning
            Toast.makeText(getApplicationContext(), "No internet connection",
                    Toast.LENGTH_LONG).show();
        }


    }


    /**
     * Stops the stream if activity destroyed
     */
    @Override
    public void onDestroy() {
        stop();
    }


    /**
     * Stops audio from the active service
     */
    private void stop() {
        if (isPlaying) {
            isPlaying = false;
            if (radioPlayer != null) {
                radioPlayer.release();
                radioPlayer = null;
            }
            stopForeground(true);
        }

        Toast.makeText(getApplicationContext(), "Stream stopped",
                Toast.LENGTH_LONG).show();
    }


    /**
     * Checks if there is a data or internet connection before starting the stream.
     * Displays Toast warning if there is no connection
     * @return online status boolean
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
