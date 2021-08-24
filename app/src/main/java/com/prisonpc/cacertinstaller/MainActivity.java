package com.prisonpc.cacertinstaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import android.widget.TextView;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView textV = new TextView( MainActivity.this );

        InputStream is = this.getResources().openRawResource(R.raw.textfile);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        try {
            // While the BufferedReader readLine is not null
            while ((readLine = br.readLine()) != null) {
                textV.setText(readLine);
                // Log.d("TEXT", readLine);
            }
            // Close the InputStream and BufferedReader
            is.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView( textV );

        // setContentView(R.layout.activity_main);
    }
}

