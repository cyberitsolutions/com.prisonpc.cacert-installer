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


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView textV = new TextView( MainActivity.this );
        Log.i("FIDME prisonpc: ", "hello world");

        InputStream cert_file = getResources().openRawResource(R.raw.cyber);

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


        Log.i("FIDME prisonpc: ", "Creating dpm");
        DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        Log.i("FIDME prisonpc: ", "Installing CA Cert");
        if (! dpm.installCaCert(null, cert_bytes.toByteArray())) { 
            textV.setText("installCaCert  Failed");
        } else {
                textV.setText("installCaCert  Succeeded");
        }
        setContentView( textV );
    }
}

