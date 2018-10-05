package com.example.antonio.limotion3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mLight;

    private Boolean isUp = true;
    private Boolean isHold = false;

    private TextView txt;
    private Float lastLux;
    private Long lastWaveTime;

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

            timerHandler2.postDelayed(this, 800);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(mLightSensorListener, mLight,
                SensorManager.SENSOR_DELAY_NORMAL);
        timerHandler2.postDelayed(timerRunnable2, 0);


        txt = findViewById(R.id.txt);

    }

    private SensorEventListener mLightSensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Float curLux = sensorEvent.values[0];
            Float deltaLux ;

            try {
                if (lastLux > curLux ){
                    deltaLux = (lastLux - curLux)/lastLux;
                } else {
                    deltaLux = (curLux - lastLux)/curLux;

                }

                if (deltaLux >= .3) {
                    if (!isUp && !isHold) {
                        isUp = true;
                        isHold = true;
                        txt.setText( "Single Wave !" );
                        long curWaveTime = System.nanoTime();
                        if (lastWaveTime != null) {
                            long deltaWave = (long) ((curWaveTime - lastWaveTime) /1e6);
                            if (deltaWave <= 1200 && deltaWave >= 300) {
                                txt.setText( "Double Wave !" );
                                Log.d("Wave", "Double" );
                            } else {
                                Log.d("Wave", "Single" );

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
