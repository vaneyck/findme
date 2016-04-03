package com.vanks.locate.findme.util;

import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by vaneyck on 03/04/2016.
 */
public class SMSUtil {
    private static String TAG = "SMSUtil";

    public static void sendCoordinatesAsSMS (Location location, String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        String message = location.getLatitude() + "," + location.getLongitude();
        Log.i(TAG, "Replying to " + phoneNumber + " with GPS coordinates " + message);
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
