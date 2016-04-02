package com.vanks.locate.findme.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.vanks.locate.findme.constant.ExtraUtil;

public class HandleIncomingSMSService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public HandleIncomingSMSService() {
        super("HandleIncomingSMSService");
    }

    GoogleApiClient googleApiClient;
    private String TAG = "HandleIncomingSMSService";
    private String phoneNumber;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            initialiseGoogleApiClient();
            phoneNumber = intent.getStringExtra(ExtraUtil.ADDRESS);
            String body = intent.getStringExtra(ExtraUtil.BODY);
            if(body.contains(getKeyword(this))) {
                Log.i(TAG, "Connecting to GoogleApiClient");
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        try{
            Location lastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
            if (lastLocation != null) {
                double latitude = lastLocation.getLatitude();
                double longitude = lastLocation.getLongitude();
                sendCoordinatesAsSMS(latitude, longitude);
            }
            googleApiClient.disconnect();
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

    private void sendCoordinatesAsSMS (double latitude, double longitude) {
        SmsManager smsManager = SmsManager.getDefault();
        String message = latitude + "," + longitude;
        Log.i(TAG, "Replying to " + phoneNumber + " with GPS coordinates " + message);
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private String getKeyword (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("locate_keyword", "findme");
    }
}
