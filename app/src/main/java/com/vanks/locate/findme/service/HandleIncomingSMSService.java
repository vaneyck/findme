package com.vanks.locate.findme.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class HandleIncomingSMSService extends IntentService {

    public HandleIncomingSMSService() {
        super("HandleIncomingSMSService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Receive SMS and check it contains keyword
        }
    }
}
