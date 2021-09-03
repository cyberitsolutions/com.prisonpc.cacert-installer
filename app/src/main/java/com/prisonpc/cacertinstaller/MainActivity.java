package com.prisonpc.cacertinstaller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import android.app.admin.DevicePolicyManager;
import android.content.Context;

// Delayed exit function
import java.util.Timer;
import java.util.TimerTask;
import java.lang.System;

// Set the wallpaper
import android.content.res.Resources;
import android.app.WallpaperManager;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("prisonpc.cacert", "Hello World");

        // FIXME: This kind of thing is fucking stupid, throw an exception properly
        boolean errored = false;

        final TextView textV = new TextView( MainActivity.this );
        setContentView( textV );

        // Read the CA cert into a byte stream array
        InputStream cert_file = getResources().openRawResource(R.raw.com_prisonpc_crt);
        // FIXME: This should probably be a "file2byteArray" helper-function
        ByteArrayOutputStream cert_bytes = new ByteArrayOutputStream();
        int i;
        // FIXME: Why does removing the try/catch cause compilation errors?
        try {
            i = cert_file.read();
            while (i != -1)
            {
                cert_bytes.write(i);
                i = cert_file.read();
            }
            cert_file.close();
        } catch (IOException e) {
            // FIXME: Does this reraise the exception as-is?
            e.printStackTrace();
        }


        // Install the CA cert
        DevicePolicyManager dpm;
        Log.d("prisonpc.cacert", "Registering DPM");
        dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        Log.d("prisonpc.cacert", "Installing CA cert");
        try {
            if (! dpm.installCaCert(null, cert_bytes.toByteArray())) {
                // Note I don't know what it means when we get this response,
                // but every code example I find for this function says to do this if/else test
                throw new Exception("Got a negative response when running installCaCert");
            } else {
                Log.i("prisonpc.cacert", "installCaCert succeeded");
                textV.setText("installCaCert Succeeded. Exiting in 3s.");
            }
        } catch (Exception e) {
            Log.e("prisonpc.cacert", "There was an exception trying to installCaCert");
            Log.e("prisonpc.cacert", "exception", e);
            textV.setText("installCaCert failed, contact Cyber IT Solutions.");

            errored = true;
        }

        // Change the wallpaper
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setResource(R.drawable.wallpaper);
        } catch (Exception e) {
            Log.e("prisonpc.cacert", "There was an exception trying to set the wallpaper");
            Log.e("prisonpc.cacert", "exception", e);
            textV.setText("Setting wallpaper failed, contact Cyber IT Solutions.");

            errored = true;
        }

        if (! errored) {
            new Timer().schedule(new TimerTask(){
                @Override
                public void run(){
                    Log.d("prisonpc.cacert", "Exiting");
                    finishAndRemoveTask();
                    System.exit(0);
                }
            }, 3000);
        }
    }
}

