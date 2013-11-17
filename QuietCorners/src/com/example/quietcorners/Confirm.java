package com.example.quietcorners;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.widget.TextView;

public class Confirm extends Activity {
    TextView lightReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Variables application = (Variables)getApplication();

        lightReading.setText("   Current Reading: " +  application.lightRating);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
