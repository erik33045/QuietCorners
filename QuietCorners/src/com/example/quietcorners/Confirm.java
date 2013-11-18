package com.example.quietcorners;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class Confirm extends Activity {
    RatingBar overallRatingBar;
    RatingBar quietRatingBar;
    RatingBar lightRatingBar;
    TextView hasOpenNetwork;
    private ImageView image;
    Button completeButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        completeButton = (Button) findViewById(R.id.completeButton);
        completeButton.setEnabled(false);
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
        lightRatingBar = (RatingBar)findViewById(R.id.rtbLightRating);
        lightRatingBar.setRating(application.lightRating);
        lightRatingBar.setFocusable(false);
        lightRatingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Over Rating Bar
        overallRatingBar = (RatingBar)findViewById(R.id.rtbOverallRating);
        lightRatingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                completeButton.setEnabled(true);
            }
        });
        overallRatingBar.setStepSize(1);

        //Wireless
        hasOpenNetwork = (TextView)findViewById(R.id.txtHasOpenNetwork);
        if(application.openNetwork) hasOpenNetwork.setText("YES");
        else hasOpenNetwork.setText("NO");

        image = (ImageView) findViewById(R.id.picture);
        image.setImageBitmap(application.cornerBitmap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void GetCompleteButtonEvent() {
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Variables application = (Variables)getApplication();

                Corner corner = new Corner();

                application.overallRating = overallRatingBar.getNumStars();

                corner.HasOpenNetwork = application.openNetwork;
                corner.LightRating = application.lightRating;
                corner.QuietRating = application.soundRating;
                corner.Image = application.cornerBitmap;
                corner.OverallRating = application.overallRating;
                corner.Lat = application.latitude;
                corner.Lng = application.longitude;

                Corner.SaveCorner(corner);

                application.soundRating = -1;
                application.lightRating = -1;
                application.internetRating = -1;
                application.openNetwork = false;
                application.cornerBitmap = null;
                application.longitude = 0;
                application.latitude = 0;
                application.overallRating = -1;

                Toast.makeText(Confirm.this,"Corner Saved!",Toast.LENGTH_LONG).show();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                android.content.Intent i = new android.content.Intent(Confirm.this, Main.class);
                startActivity(i);
             }

        });
    }
}