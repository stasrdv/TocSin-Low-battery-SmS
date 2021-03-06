package com.example.stas.tocsin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by stas on 11/09/16.
 *
 * This class will start the service on start up (after device boot)
 */

public class StartMyServiceAtBootReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Intent serviceIntent = new Intent(context, MyService.class);
            context.startService(serviceIntent);
        }
    }
}