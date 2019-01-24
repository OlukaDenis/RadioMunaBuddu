package com.mcdenny.radiomunabuddu;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton mPlay, mStop;
    SeekBar progressBar;
    //http://mp3hdfm32.hala.jo:8132
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Playing Radio...");

        initializeViews();
        initializeMediaPlayer();
    }

    private void initializeViews() {
        mPlay = (FloatingActionButton) findViewById(R.id.play);
        mStop = (FloatingActionButton) findViewById(R.id.stop);
        progressBar = (SeekBar) findViewById(R.id.radio_progressbar);
        progressBar.setMax(100);

        mPlay.setOnClickListener(v -> playRadio());
        mStop.setOnClickListener(v -> stopRadio());
    }

    private void initializeMediaPlayer() {

        mediaPlayer = new MediaPlayer(); //init the media player
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //getting audio from internet
        try {
            String URL = "http://markswist.com/markpersonal/B.O.B.-The_Adventures_Of_Bobby_Ray/04-b.o.b.-airplanes_(ft._hayley_williams_of_paramore).mp3";
            //String URL = "http://mp3hdfm32.hala.jo:8132";
            mediaPlayer.setDataSource(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                progressBar.setSecondaryProgress(percent);
                Log.i("Buffering", "" + percent);
            }
        });

    }

    private void playRadio() {
        try {
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Radio playing...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopRadio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            initializeMediaPlayer();
            progressBar.resetPivot();
            Toast.makeText(MainActivity.this, "Radio stopped..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
}
