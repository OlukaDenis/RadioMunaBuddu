package com.premar.radiomunabuddu;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static com.premar.radiomunabuddu.AppUtils.RMB_CHANNEL_ID;
import static com.premar.radiomunabuddu.AppUtils.RMB_NOTIFICATION_ID;

public class RadioMediaPlayerService extends Service implements
        AudioManager.OnAudioFocusChangeListener {
    //Variables
    private boolean isPlaying = false;
    private MediaPlayer radioPlayer; //The media player instance
    private static int classID = 579; // just a number
    public static String START_PLAY = "START_PLAY";
    AudioManager audioManager;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
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
        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Starts radio URL stream
     */
    private void play() {

        //Check connectivity status
        if (AppUtils.isNetworkAvailable(this)) {
            //Check if player already streaming
            if (!isPlaying) {
                isPlaying = true;


                //create the notification channel for the app
                createWorkerNotificationChannel(notificationManager);

                Intent radioIntent = new Intent(getApplicationContext(), HomeActivity.class);
                radioIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent workerPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        RMB_NOTIFICATION_ID,
                        radioIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                builder = getNotificationBuilder(getApplicationContext());
                builder.setContentIntent(workerPendingIntent);

                notificationManager.notify(RMB_NOTIFICATION_ID, builder.build());


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
                    radioPlayer.setOnBufferingUpdateListener((mp, percent) -> Log.i("Buffering", "" + percent));
                }

                radioPlayer.prepareAsync();
                radioPlayer.setOnPreparedListener(mp -> {
                    radioPlayer.start(); //Start radio stream
                });

                Notification notification = builder.build();
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

    private void createWorkerNotificationChannel(NotificationManager notificationManager){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Notification channel targeting Android 8 and above
            NotificationChannel channel = new NotificationChannel(RMB_CHANNEL_ID,
                    "Worker Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.setLightColor(Color.BLACK);
            channel.setDescription("Notification from Radio MB FM");

            notificationManager.createNotificationChannel(channel);
        }
    }



    private NotificationCompat.Builder getNotificationBuilder(Context context){
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.rmb);

        return new NotificationCompat.Builder(context, RMB_CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Radio MB FM")
                .setContentText("You're listening to Radio MB FM")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);
    }


}
