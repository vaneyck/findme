package com.vanks.locate.findme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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
    int locationWaitThreshold = 30000;

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
        if(millisecondsSinceLastUpdate > locationWaitThreshold ||
                currentLocation.getProvider() == LocationManager.GPS_PROVIDER) {
            SMSUtil.sendCoordinatesAsSMS(currentLocation, address);
            HandleIncomingSMSService.stopLocationUpdates();
        }
    }
}
