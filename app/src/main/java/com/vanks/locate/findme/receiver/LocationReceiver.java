package com.vanks.locate.findme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vanks.locate.findme.constant.ExtraUtil;
import com.vanks.locate.findme.service.HandleIncomingSMSService;
import com.vanks.locate.findme.util.NotificationUtil;
import com.vanks.locate.findme.util.SMSUtil;

import java.util.Date;

/**
 * Created by vaneyck on 03/04/2016.
 */
public class LocationReceiver extends BroadcastReceiver {
    String TAG = "LocationReceiver";
    static Location currentLocation;
    static long lastLocationUpdate;

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
        String address = intent.getStringExtra(ExtraUtil.ADDRESS);
        Log.i(TAG, "Received " + location);
        if(currentLocation == null){
            currentLocation = location;
            lastLocationUpdate = new Date().getTime();
        } else {
            if(location != null) {
                if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                    if(location.getAccuracy() < currentLocation.getAccuracy()) {
                        Log.i(TAG, "Setting currentLocation : " + location);
                        currentLocation = location;
                    }
                }
                if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                    Log.i(TAG, "Setting currentLocation : " + location);
                    currentLocation = location;
                }
            }
        }
        long millisecondsSinceLastUpdate = new Date().getTime() - lastLocationUpdate;
        Log.i(TAG, "Milliseconds since last location update " + millisecondsSinceLastUpdate);
        if(currentLocation == null && millisecondsSinceLastUpdate > getLocationWaitThreshold(context)) {
            String errorMessage = "Could not retrieve coordinates. Try again.";
            SMSUtil.sendMessage(address, errorMessage);
            HandleIncomingSMSService.stopLocationUpdates();
            NotificationUtil.createAndShow(context, "FindMe", errorMessage);
            currentLocation = null;
            return;
        }
        if(millisecondsSinceLastUpdate > getLocationWaitThreshold(context) ||
                (currentLocation != null && currentLocation.getProvider().equals(LocationManager.GPS_PROVIDER))) {
            SMSUtil.sendCoordinatesAsSMS(currentLocation, address);
            String detailsMessage = "accuracy = " + currentLocation.getAccuracy() + "\n" +
                    "provider = " + currentLocation.getProvider();
            SMSUtil.sendMessage(address, detailsMessage);
            String notificationMessage = address + " : " + currentLocation.getLatitude() + ","
                    + currentLocation.getLongitude();
            NotificationUtil.createAndShow(context, "FindMe", notificationMessage);
            HandleIncomingSMSService.stopLocationUpdates();
            currentLocation = null;
        }
    }

    int getLocationWaitThreshold(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int timeout = Integer.parseInt(sharedPreferences.getString("gps_timeout", "60"));
        return timeout * 1000;
    }
}
