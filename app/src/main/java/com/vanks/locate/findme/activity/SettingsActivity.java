package com.vanks.locate.findme.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.vanks.locate.findme.R;

/**
 * Created by vaneyck on 02/04/2016.
 */
public class SettingsActivity extends PreferenceActivity {
    final static int REQUEST_PERMISSIONS_ID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    private void createPermissionsRequestDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.request_permissions_message)
                .setTitle(R.string.request_permissions_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                requestForNeededAppPermissions();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SettingsActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (atLeastOnePermissionNotGranted()) {
            createPermissionsRequestDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_ID: {
                if (grantResults.length == 0) {
                    break;
                }
                boolean goToSettingsToAppovePermission = false;
                boolean allPermissionsAccepted = true;
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    Log.i("SettingsActivity", permissions[i] + " : " + grantResults[i]);
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        allPermissionsAccepted = false;
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this, permission);
                        if (!showRationale) {
                            Log.i("PermissionActivity","Denied + Do Not Ask : " + permissions[i]);
                            goToSettingsToAppovePermission = true;
                        }
                    }
                }

                if (!allPermissionsAccepted) {
                    createPermissionsRequestDialog();
                }
                break;
            }
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestForNeededAppPermissions () {
        if (atLeastOnePermissionNotGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.SEND_SMS
                    }, REQUEST_PERMISSIONS_ID);
        }
    }

    private boolean atLeastOnePermissionNotGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED;
    }
}