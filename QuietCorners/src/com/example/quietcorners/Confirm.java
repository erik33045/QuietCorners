package com.example.quietcorners;

import android.app.Activity;
import android.content.*;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class Confirm extends Activity {
    RatingBar overallRatingBar;
    RatingBar quietRatingBar;
    RatingBar lightRatingBar;
    TextView hasOpenNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        GetCompleteButtonEvent();
        Variables application = (Variables)getApplication();

        //Sound Rating Bar
        quietRatingBar = (RatingBar)findViewById(R.id.rtbQuietRating);
        quietRatingBar.setRating(application.soundRating);
        quietRatingBar.setFocusable(false);
        quietRatingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Light Rating Bar
        lightRatingBar = (RatingBar)findViewById(R.id.rtbLighRating);
        lightRatingBar.setRating(application.lightRating);
        lightRatingBar.setFocusable(false);
        lightRatingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Over Rating Bar
        overallRatingBar = (RatingBar)findViewById(R.id.rtbOverallRating);
        overallRatingBar.setStepSize(1);

        //Wireless
        hasOpenNetwork = (TextView)findViewById(R.id.txtHasOpenNetwork);
        if(application.openNetwork) hasOpenNetwork.setText("YES");
        else hasOpenNetwork.setText("NO");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void GetCompleteButtonEvent() {
        Button button = (Button) findViewById(R.id.completeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Corner corner = new Corner();
                Variables application = (Variables)getApplication();

                application.overallRating = overallRatingBar.getNumStars();

                corner.HasOpenNetwork = application.openNetwork;
                corner.LightRating = application.lightRating;
                corner.QuietRating = application.soundRating;
                corner.Image = application.cornerBitmap;
                corner.OverallRating = application.overallRating;
                corner.Lat = application.latitude;
                corner.Lng = application.longitude;

                android.content.Intent i = new android.content.Intent(Confirm.this, Main.class);
                startActivity(i);
            }

            ;
        });
    }
}
