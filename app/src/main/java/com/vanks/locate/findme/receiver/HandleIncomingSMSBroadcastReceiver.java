package com.vanks.locate.findme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.vanks.locate.findme.constant.ExtraUtil;
import com.vanks.locate.findme.service.HandleIncomingSMSService;

/**
 * Created by vaneyck on 02/04/2016.
 */
public class HandleIncomingSMSBroadcastReceiver extends BroadcastReceiver {

    private String TAG = "HandleIncomingSMSBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null) {
            Object[] smsExtra = (Object[])extras.get("pdus");
            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();
                Log.i(TAG, "Received " + address + ":" + body);

                Bundle bundle = new Bundle();
                bundle.putString(ExtraUtil.ADDRESS, address);
                bundle.putString(ExtraUtil.BODY, body);

                Intent handleIncomingSMSIntent = new Intent(context, HandleIncomingSMSService.class);
                handleIncomingSMSIntent.putExtras(bundle);
                context.startService(handleIncomingSMSIntent);
            }
        }
    }
}
