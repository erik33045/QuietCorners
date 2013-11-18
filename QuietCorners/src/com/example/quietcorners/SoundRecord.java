package com.example.quietcorners;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class SoundRecord extends Activity {
    TextView decibels;
    RatingBar soundRatingBar;
    MediaRecorder decRecorder = null;
    private double ema = 0.0;
    private double soundLevel = 0;
    private int soundRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundrecord);
        decibels = (TextView)findViewById(R.id.rec_dB);
        soundRatingBar = (RatingBar)findViewById(R.id.rating_Sound);

        decibels.setText("   Recording: 0.00 dB");

        startRecord(); //Go ahead and start the recorder on creation.
        GetSaveButtonAndBindClickEvent();
        GetRecordButtonAndBindClickEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        stopRecord();
    }


    public void startRecord() {
        if (decRecorder == null) {
            decRecorder = new MediaRecorder();
            decRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            decRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            decRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            decRecorder.setOutputFile("/dev/null");
            try {
                decRecorder.prepare();
            } catch (java.io.IOException ioe) {
                Toast.makeText(SoundRecord.this, "prepare() failed", Toast.LENGTH_LONG).show();
            }
            decRecorder.start();
            soundRatingBar.setFocusable(false);
            soundRatingBar.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            updateDisplay();
        }
    }

    public void stopRecord() {
        if (decRecorder != null) {
            decRecorder.stop();
            decRecorder.release();
            decRecorder = null;
        }
    }

    private void GetSaveButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.sav_Sound);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRating();
                Intent i = new Intent(SoundRecord.this, Record.class);
                startActivity(i);
            }
        });
    }

    private void GetRecordButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.get_sound);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundLevel = getAmplitude();
                calculateRating();
                updateDisplay();
            }
        });
    }

    public double getDecibels() {
        return -1 * 20 * Math.log10(getAmplitude() / 32767.0);
    }

    public double getAmplitude() {
        double amp;
        if (decRecorder != null) {
            amp = decRecorder.getMaxAmplitude();
            ema = 0.6 * amp + (1.0 - 0.6) * ema;
            return ema;
        } else return 0;
    }

    public void updateDisplay() {
        double value = soundLevel;
        DecimalFormat df = new DecimalFormat("#.##");
        decibels.setText("   Recording: " + String.valueOf(df.format(value)) + " dB");
    }

    private void saveRating() {
        Variables application = (Variables)getApplication();
        application.soundRating = soundRating;
    }

    private int calculateRating() {
        int rating = 5;
        if (soundLevel >= 1500) rating = 4;
        if (soundLevel >= 3500) rating = 3;
        if (soundLevel >= 6000) rating = 2;
        if (soundLevel >= 9000) rating = 1;
        if (soundLevel >= 15000) rating = 0;
        soundRatingBar.setRating(rating);
        soundRating = rating;
        return rating;
    }
}
