package com.example.antonio.limotion3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mLight;

    private Boolean isUp = true;
    private Boolean isHold = false;
    private Boolean isPlay = false;

    private TextView txt, stat;
    private Float lastLux ;
    private Long lastWaveTime;
    private Long lastDown = 0l;

    private Integer countWave = 0;
    private Integer cursorPlay = 0;
    MediaPlayer mp;
    ImageView whoamiwith;
    Integer downCounter = 0;

    Integer audio[] = {R.raw.audio_0,R.raw.audio_1,R.raw.audio_2,R.raw.audio_3};
    Integer cover[] = {R.raw.cover_0,R.raw.cover_1,R.raw.cover_2,R.raw.cover_3};


    Handler timerHandler1 = new Handler();
    Runnable timerRunnable1 = new Runnable() {

        @Override
        public void run() {

            stat.setText( "" );
//            timerHandler1.postDelayed(this, 0);

        }
    };


    Handler timerHandler2 = new Handler();
    Runnable timerRunnable2 = new Runnable() {

        @Override
        public void run() {
            if (isHold){
                isHold = false;
            }

            timerHandler2.postDelayed(this, 500);

        }
    };

    Handler timerHandler3 = new Handler();
    Runnable timerRunnable3 = new Runnable() {

        @Override
        public void run() {
            switch (countWave){
                case 1:
                    stat.setText( "a Wave! " );
                    Log.d("WAVE","SINGLE");
                    togglePlay();
                    break;
                case 2:
                    stat.setText( "Double Wave! Next Song" );

                    Log.d("WAVE","DOUBLE");
                    nextSong();

                    break;
                case 3:
                    stat.setText( "Triple Wave ! Prev Song" );
                    Log.d("WAVE","TRIPLE");
                    cursorPlay = (cursorPlay + 1) % 4;
                    prevSong();


                    break;
            }


            timerHandler1.postDelayed(timerRunnable1, 2000);

        }
    };

    public void togglePlay(){
        isPlay = ! isPlay;
        if (isPlay) {
            mp.start();
            txt.setText( "Playing" );
        } else {
            mp.pause();
            txt.setText( "Pause" );
        }
    }

    public void nextSong() {
        mp.stop();
        cursorPlay = (cursorPlay + 1) % audio.length;
        mp = MediaPlayer.create(this, audio[cursorPlay]);
        whoamiwith.setImageResource(cover[cursorPlay]);
        mp.start();
        txt.setText( "Playing" );
        isPlay = true;
    }

    public void prevSong() {
        mp.stop();
        cursorPlay = (cursorPlay - 2) % audio.length;
        if (cursorPlay < 0) {
            cursorPlay = (audio.length -1);
        }
        mp = MediaPlayer.create(this, audio[cursorPlay]);
        whoamiwith.setImageResource(cover[cursorPlay]);
        mp.start();
        txt.setText( "Playing" );
        isPlay = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(mLightSensorListener, mLight,
                SensorManager.SENSOR_DELAY_FASTEST);
        timerHandler2.postDelayed(timerRunnable2, 0);


        txt = findViewById(R.id.txt);
        mp = MediaPlayer.create(this, audio[cursorPlay]);
        lastWaveTime = System.currentTimeMillis();

        whoamiwith = (ImageView)findViewById(R.id.whoamiwith);
        whoamiwith.setImageResource(cover[cursorPlay]);
        stat = findViewById(R.id.stat);

    }

    private SensorEventListener mLightSensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Float curLux = sensorEvent.values[0];
            Float deltaLux = 0f;
            Long deltaWave = 0l;
            Log.d("curLux", String.valueOf(curLux));

            try {

                if (lastLux > curLux ){
                    deltaLux = (lastLux - curLux)/lastLux;
                } else {
                    deltaLux = (curLux - lastLux)/curLux;
                }

                if (deltaLux >= .03) {
                    if (!isUp && ! isHold) {
                        isUp = true;
                        isHold = true;

                        long curWaveTime = System.currentTimeMillis();

                        if (lastWaveTime != null ) {
                            deltaWave = (long) ((curWaveTime - lastWaveTime));

                            if (deltaWave <= 2200 && deltaWave >= 100) {
                                countWave = (countWave + 1) % 4;

                            } else {
                                countWave = 1;
                                timerHandler3.postDelayed(timerRunnable3, 4000);

                            }
                            Log.d("Wave", countWave + ", "+deltaLux+","+deltaWave );

                        }

                        lastWaveTime = curWaveTime;

                    } else if (isUp && !isHold){
                        isUp = false;


                    }
                }


            } catch (Exception e) {
                lastLux = curLux;
                downCounter = 0;
            }

            if (lastLux> curLux) {
                lastLux = curLux;
            } else if (deltaLux >=0.10) {
                lastLux = curLux;


            }




        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }


    };

}
