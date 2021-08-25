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

// For the uninstall part that still requires user input
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("prisonpc.cacert: ", "Hello World");

        final TextView textV = new TextView( MainActivity.this );

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


        Log.i("prisonpc.cacert: ", "Registering DPM");
        DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        Log.i("prisonpc.cacert: ", "Installing CA cert");
        if (! dpm.installCaCert(null, cert_bytes.toByteArray())) { 
            Log.i("prisonpc.cacert: ", "installCaCert failed apparently");
            textV.setText("installCaCert Failed");
            setContentView( textV );
        } else {
            Log.i("prisonpc.cacert: ", "installCaCert succeeded");
            textV.setText("installCaCert Succeeded. Uninstalling myself.");
            setContentView( textV );

            Log.i("prisonpc.cacert: ", "Trying to uninstall myself");
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:com.prisonpc.cacertinstaller"));
            startActivity(uninstallIntent);

        }
    }
}

