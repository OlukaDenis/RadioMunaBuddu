package com.mcdenny.radiomunabuddu;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageView mPlay, mStop, mPause;
    private static String URL = "http://markswist.com/markpersonal/B.O.B.-The_Adventures_Of_Bobby_Ray/04-b.o.b.-airplanes_(ft._hayley_williams_of_paramore).mp3";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlay = (ImageView) findViewById(R.id.play);
        mStop = (ImageView) findViewById(R.id.stop);
        mPause = (ImageView) findViewById(R.id.pause);

        mediaPlayer = new MediaPlayer(); //init the media player
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //playing audio
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting audio from internet
                try {
                    mediaPlayer.setDataSource(URL);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //buffer the audio
                try{
                    mediaPlayer.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                //start playing the audio file
                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Song playing...", Toast.LENGTH_SHORT).show();
            }
        });

        //pausing the audio
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                Toast.makeText(MainActivity.this, "Song Paused..", Toast.LENGTH_SHORT).show();
            }
        });


        //stopping the audio
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Toast.makeText(MainActivity.this, "Song stopped..", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
