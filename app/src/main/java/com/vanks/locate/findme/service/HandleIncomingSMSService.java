package com.vanks.locate.findme.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.vanks.locate.findme.constant.ExtraUtil;
import com.vanks.locate.findme.receiver.LocationReceiver;

public class HandleIncomingSMSService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public HandleIncomingSMSService() {
        super("HandleIncomingSMSService");
    }

    GoogleApiClient googleApiClient;
    static LocationManager locationManager;
    static PendingIntent locationIntent;
    private String TAG = "HandleIncomingSMSService";
    private String phoneNumber;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            initialiseGoogleApiClient();
            phoneNumber = intent.getStringExtra(ExtraUtil.ADDRESS);
            String body = intent.getStringExtra(ExtraUtil.BODY).trim();
            if(body.contains(getKeyword(this))) {
                Log.i(TAG, "Connecting to GoogleApiClient");
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        try {
            Intent intent = new Intent(this, LocationReceiver.class);
            intent.putExtra(ExtraUtil.ADDRESS, phoneNumber);
            locationIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationIntent);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationIntent);
        } catch (SecurityException se) {
            Log.e(TAG, se.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    private void initialiseGoogleApiClient () {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private String getKeyword (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("locate_keyword", "findme").trim();
    }

    public static void stopLocationUpdates () {
        locationManager.removeUpdates(locationIntent);
    }
}
