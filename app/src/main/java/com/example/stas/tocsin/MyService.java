package com.example.stas.tocsin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;

/**
 * Created by stas on 09/09/16.
 *
 * This is my Unbound service class
 */
public class MyService extends Service {


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    boolean sent = false;

    public void onCreate() {

        super.onCreate();

    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        return START_STICKY;
        /**
         * START_STICKY- tells the system to create a fresh copy of the service,
         * when sufficient memory is available, after it recovers from low memory.
         * Here you will lose the results that might have computed before.
         */
    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        //When Event is published, onReceive method is called
        public void onReceive(Context c, Intent i) {

            int level = i.getIntExtra("level", 0);
            //get user's critical level
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String critlevel = prefs.getString("defaultLevel", null);

            if (level < Integer.parseInt(critlevel) && sent == false) {
                addNotification();
                sendSMS(prefs.getString("phone", null), prefs.getString("msg", null));
                sent = true;

            }
            if (level > Integer.parseInt(critlevel) + 10) {
                sent = false;
            }
        }

    };


    public void onStop() {


    }

    public void onPause() {


    }

    public void onDestroy() {

    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);


        } catch (Exception ex) {

            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.chat)
                        .setContentTitle("TocSin Agent")
                        .setContentText("Automatic SmS was sent")
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

}
