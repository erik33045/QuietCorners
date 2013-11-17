package com.example.quietcorners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Application;

public class LumenRecord extends Activity {
    TextView textMax, textReading;
    float max;
    int rating;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lumenrecord);
        textMax = (TextView)findViewById(R.id.max);
        textReading = (TextView)findViewById(R.id.reading);


        GetSaveButtonAndBindClickEvent();

        SensorManager sensorManager
                = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor
                = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null){
            Toast.makeText(LumenRecord.this,
                    "No Light Sensor! quit-",
                    Toast.LENGTH_LONG).show();
        }else{
            sensorManager.registerListener(lightSensorEventListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }
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
                float currentReading = event.values[0];
                if (currentReading > max){
                    max =  event.values[0];
                    textMax.setText("   Max Reading: " + String.valueOf(max));
                    }
                    textReading.setText("   Current Reading: " + String.valueOf(currentReading));
            }
        }

    };

    private void GetSaveButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Rating conversion
                if (max = 0) rating = 0;
                if (max > 0) rating = 1;
                if(max>50) rating = 2;
                if(max>100) rating = 3;
                if(max>1000) rating = 5;
                if(max>10000) rating = 4;

                Variables application = (Variables)getApplication();
                application.lightRating = rating;
                Intent i = new Intent(LumenRecord.this, Record.class);
                startActivity(i);
            }
        });
    }
}