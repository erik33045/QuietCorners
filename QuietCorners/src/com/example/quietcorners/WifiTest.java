package com.example.quietcorners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class WifiTest extends Activity {
    TextView wifiText;
    /** Anything worse than or equal to this will show 0 bars. */
    private static final int MIN_RSSI = -100;
    /** Anything better than or equal to this will show the max bars. */
    private static final int MAX_RSSI = -55;
    private boolean wifiEnabled = false;
    private int wifiRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifitest);
        wifiText = (TextView)findViewById(R.id.rec_Wifi);

        Button testWifiButton = (Button) findViewById(R.id.test_wifi);
        testWifiButton.setEnabled(false);

        GetRecordButtonAndBindClickEvent();
        GetSaveButtonAndBindClickEvent();

        final WifiManager wifiTester = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        if (wifiTester.isWifiEnabled()) {
            Toast.makeText(WifiTest.this, "Wifi-enabled", Toast.LENGTH_LONG).show();
            wifiTester.startScan();
            testWifiButton.setEnabled(true);

            /* Update the display twice to avoid the initial value, 0, of Max_Amplitude() */
            updateDisplay();
            updateDisplay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /* Main utility function to retrieve the signal strength. */
    private int getSignalStrength() {
        int signal = 0;

        final WifiManager wifiTester = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        int state = wifiTester.getWifiState();

        if (state == WifiManager.WIFI_STATE_ENABLED) {
            List<ScanResult> wifiPoints = wifiTester.getScanResults();
            if (wifiPoints == null) return signal;

            for (ScanResult point : wifiPoints) {
                if(point != null && wifiTester != null && wifiTester.getConnectionInfo() != null && point.BSSID != null
                        && point.BSSID.equals(wifiTester.getConnectionInfo().getBSSID())) {

                    int level = 0;
                    level = calculateSignalLevel(wifiTester.getConnectionInfo().getRssi(), point.level);

                    if(level != 0 && point.level != 0) {
                        // Convert strength into percentage
                        int difference = level * 100 / point.level;
                        if(difference >= 100)
                            signal = 5;
                        else if(difference >= 80)
                            signal = 4;
                        else if(difference >= 60)
                            signal = 3;
                        else if(difference >= 40)
                            signal = 2;
                        else if(difference >= 20)
                            signal = 1;
                    }
                }

            }
        }
        return signal;
    }

    /* Needed to prevent division by zero. */
    private int calculateSignalLevel(int rssi, int numLevels) {
        if(rssi <= MIN_RSSI) {
            return 0;
        } else if(rssi >= MAX_RSSI) {
            return numLevels - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (numLevels - 1);
            if(inputRange != 0)
                return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
        }
        return 0;
    }

    private void GetRecordButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.test_wifi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDisplay();
            }
        });
    }

    private void GetSaveButtonAndBindClickEvent() {
        Button button = (Button) findViewById(R.id.sav_Wifi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRating();
                Intent i = new Intent(WifiTest.this, Record.class);
                startActivity(i);
            }
        });
    }

    private void updateDisplay() {
        int signalStrength = getSignalStrength();
        if (signalStrength != -1) {
            wifiText.setText("   Signal: " + String.valueOf(signalStrength) + " bars");
            wifiEnabled = true;
        }
        else {
            wifiText.setText("   Signal: None");
            wifiEnabled = false;
        }
        wifiRating = signalStrength;
    }

    private void saveRating() {
        Variables application = (Variables)getApplication();
        application.internetRating = wifiRating;
        application.openNetwork = wifiEnabled;
    }
}