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
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mLight;

    private Boolean isUp = true;
    private Boolean isHold = false;

    private TextView txt;
    private Float lastLux;
    private Long lastWaveTime;
    private Integer countWave = 0;
    MediaPlayer mp;


    Handler timerHandler1 = new Handler();
    Runnable timerRunnable1 = new Runnable() {

        @Override
        public void run() {

            txt.setText( "" );
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(mLightSensorListener, mLight,
                SensorManager.SENSOR_DELAY_GAME);
        timerHandler2.postDelayed(timerRunnable2, 0);


        txt = findViewById(R.id.txt);
        mp = MediaPlayer.create(this, R.raw.audio_1);
        lastWaveTime = System.currentTimeMillis();

    }

    private SensorEventListener mLightSensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Float curLux = sensorEvent.values[0];
            Float deltaLux ;
            Log.d("curLux", String.valueOf(curLux));



            try {
                if (lastLux > curLux ){
                    deltaLux = (lastLux - curLux)/lastLux;
                } else {
                    deltaLux = (curLux - lastLux)/curLux;

                }
                Log.d("deltaLux", String.valueOf(deltaLux));
                if (deltaLux >= .05) {
                    if (!isUp && ! isHold) {
                        isUp = true;
                        isHold = true;
                        txt.setText( "Single Wave !" );
                        long curWaveTime = System.currentTimeMillis();

                        Log.d("curWaveTime", String.valueOf(curWaveTime));

                        if (lastWaveTime != null ) {
                            long deltaWave = (long) ((curWaveTime - lastWaveTime));


                            Log.d("deltaWave", String.valueOf(deltaWave));
                            if (deltaWave <= 2100 && deltaWave >= 1400) {
                                countWave = (countWave + 1) % 4;
                                switch (countWave){
                                    case 1:
                                        Log.d("Wave", "Single" );
                                        txt.setText( "a Wave !" );
                                        break;
                                    case 2:
                                        Log.d("Wave", "Double" );
                                        txt.setText( "Double Wave !" );
                                        break;
                                    case 3:
                                        Log.d("Wave", "Triple" );
                                        txt.setText( "Triple Wave !" );
                                        countWave = 0;
                                        break;
                                }


//                                mp.pause();


                            } else {
                                countWave = 1;
                                Log.d("Wave", "Single" );
//                                mp.start();

                            }
                        }
                        lastWaveTime = curWaveTime;
                        timerHandler1.postDelayed(timerRunnable1, 1000);
                    } else if (isUp && !isHold){
                        isUp = false;


                    }
                }

            } catch (Exception e) {

            }
            lastLux = curLux;

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }


    };

}
