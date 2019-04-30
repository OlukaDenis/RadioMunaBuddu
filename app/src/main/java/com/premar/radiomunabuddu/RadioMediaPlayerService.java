package com.premar.radiomunabuddu;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class RadioMediaPlayerService extends Service implements
        AudioManager.OnAudioFocusChangeListener {
    //Variables
    private boolean isPlaying = false;
    private MediaPlayer radioPlayer; //The media player instance
    private static int classID = 579; // just a number
    public static String START_PLAY = "START_PLAY";
    AudioManager audioManager;
    //Media session
    MediaSession mSession;

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
        //Request audio focus
        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    /**
     * Starts radio URL stream
     */
    private void play() {

        //Check connectivity status
        if (isOnline()) {
            //Check if player already streaming
            if (!isPlaying) {
                isPlaying = true;

                //Return to the current activity
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                mSession.setSessionActivity(pi);

                //Build and show notification for radio playing
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.buddu3);
                Notification notification = new NotificationCompat.Builder(this, "ID")
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setTicker("Radio Munnabuddu USA")
                        .setContentTitle(settings.getRadioName())
                        .setContentText(settings.getMainNotificationMessage())
                        .setSmallIcon(R.drawable.ic_radio_black_24dp)
                        //.addAction(R.drawable.ic_play_arrow_white_64dp, "Play", pi)
                       // .addAction(R.drawable.ic_pause_black_24dp, "Pause", pi)
                        .setLargeIcon(largeIcon)
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
        removeAudioFocus();
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

        Toast.makeText(getApplicationContext(), "Radio stopped",
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

    @Override
    public void onAudioFocusChange(int focusChange) {

        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                // if (radioPlayer == null) initMediaPlayer();
                if (!radioPlayer.isPlaying()) radioPlayer.release();

                radioPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (radioPlayer.isPlaying()) radioPlayer.stop();
                radioPlayer.release();
                //radioPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (radioPlayer.isPlaying()) radioPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (radioPlayer.isPlaying()) radioPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /**
     * AudioFocus
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }


}
