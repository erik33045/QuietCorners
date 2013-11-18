package com.example.quietcorners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class LumenRecord extends Activity {
    TextView textMax, textReading;
    float currentReading;
    int rating;
    RatingBar lightRatingBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumenrecord);

        textMax = (TextView)findViewById(R.id.max);
        textReading = (TextView)findViewById(R.id.reading);
        lightRatingBar = (RatingBar)findViewById(R.id.rtbLightRating);

        SensorManager sensorManager
                = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor
                = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null){
            lightRatingBar.setStepSize(1);
            Toast.makeText(LumenRecord.this,
                    "No light sensor.  Rate manually.",
                    Toast.LENGTH_LONG).show();
        }else{
            lightRatingBar.setFocusable(false);
            lightRatingBar.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            sensorManager.registerListener(lightSensorEventListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }

        GetSaveButtonAndBindClickEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    SensorEventListener lightSensorEventListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if(event.sensor.getType()==Sensor.TYPE_LIGHT){
                    currentReading = event.values[0];
                    //Rating conversion
                    if (currentReading == 0.0) rating = 0;
                    if (currentReading > 0.0) rating = 1;
                    if(currentReading>=50) rating = 2;
                    if(currentReading>=100) rating = 3;
                    if(currentReading>=1000) rating = 5;
                    if(currentReading>=10000) rating = 4;
                    lightRatingBar.setRating(rating);
                    }
                    textReading.setText("   Current Reading: " + String.valueOf(currentReading));
        }

    };

    private void GetSaveButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Variables application = (Variables)getApplication();
                application.lightRating = rating;

                Intent i = new Intent(LumenRecord.this, Record.class);
                startActivity(i);
            }
        });
    }
}