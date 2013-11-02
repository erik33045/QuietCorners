package com.example.quietcorners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.media.AudioRecord;

import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SoundRecord extends Activity {
    MediaRecorder decRecorder;
    private static double ema = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundrecord);

    }
    protected void onResume() {
        super.onResume();
        startRecord();
    }
    protected void onPause() {
        super.onPause();
        stopRecord();
    }


    public void startRecord() {
        if (decRecorder == null) {
            decRecorder = new MediaRecorder();
            decRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            decRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            decRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            decRecorder.setOutputFile("/debug/null");

            try {
                decRecorder.prepare();
            } catch (java.io.IOException ioe) {
                //nothing for now
            }

            decRecorder.start();
        }
    }

    public void stopRecord() {
        if (decRecorder != null) {
            decRecorder.stop();
            decRecorder.release();
            decRecorder = null;
        }
    }

    public double getDecibels(double amp) {
        return 20 * Math.log10(getAmplitude() / amp);
    }

    public double getAmplitude() {
        double amp;
        if (decRecorder != null) {
            amp = decRecorder.getMaxAmplitude();
            ema = 0.6 * amp + (1.0 - 0.6) * ema;
            return ema;
        } else return 0;
    }
}
