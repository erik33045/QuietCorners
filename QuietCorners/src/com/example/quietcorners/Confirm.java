package com.example.quietcorners;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class Confirm extends Activity {
    TextView lightReading;
    RatingBar overallRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        GetCompleteButtonEvent();
        overallRatingBar = (RatingBar)findViewById(R.id.overallRating);
        overallRatingBar.setStepSize(1);
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
