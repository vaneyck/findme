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
            if(location.getProvider() == LocationManager.GPS_PROVIDER) {
                currentLocation = location;
            }
        }
        long millisecondsSinceLastUpdate = new Date().getTime() - lastLocationUpdate;
        Log.i(TAG, "milliseconds since last location update " + millisecondsSinceLastUpdate);
        if(millisecondsSinceLastUpdate > getLocationWaitThreshold(context) ||
                currentLocation.getProvider() == LocationManager.GPS_PROVIDER) {
            SMSUtil.sendCoordinatesAsSMS(currentLocation, address);
            HandleIncomingSMSService.stopLocationUpdates();
            currentLocation = null;
        }
    }

    int getLocationWaitThreshold(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int timeout = sharedPreferences.getInt("gps_timeout_title", 60);
        return timeout * 1000;
    }
}
