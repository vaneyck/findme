package com.vanks.locate.findme.util;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.vanks.locate.findme.R;

import java.util.Date;

/**
 * Created by vaneyck on 06/04/2016.
 */
public class NotificationUtil {
    public static void createAndShow(Context context, String title, String text) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text);
        int notificationId = (int) new Date().getTime();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
