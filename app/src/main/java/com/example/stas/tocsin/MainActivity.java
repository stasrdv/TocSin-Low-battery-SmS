package com.example.stas.tocsin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

/**
 * Created by stas on 11/09/16.
 /**
 * 1.Start the service
 * 2.Register the receiver which triggers event when battery charge is changed
 */

public class MainActivity extends Activity {


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Switch mySwitch;
    private CircleMenu circleMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);

        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        //load shared prefs
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //get battery critical level
        String defaultLevel = prefs.getString("defaultLevel", null);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar);
        //Set progress level with battery % value
        pb.setProgress(Integer.parseInt(defaultLevel));

        TextView blevel = (TextView) findViewById(R.id.blevel);
        blevel.setText("Battery Critical Level" + " " + defaultLevel + "%");
        mySwitch = (Switch) findViewById(R.id.switch1);

        mySwitch.setChecked(false);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    mySwitch.setText("Agent is OFF");
                    Toast.makeText(MainActivity.this,
                            "TocSin service is stopped ", Toast.LENGTH_LONG).show();
                    stopService(new Intent(MainActivity.this, MyService.class));

                } else {
                    mySwitch.setText("Agent is ON");
                    startService(new Intent(MainActivity.this, MyService.class));
                    Toast.makeText(MainActivity.this,
                            "TocSin service is now running ", Toast.LENGTH_LONG).show();
                }

            }
        });



        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
        circleMenu.setMainMenu(Color.parseColor("#258CFF"), R.mipmap.icon_setting, R.mipmap.icon_cancel)
                .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.icon_home)
                .addSubMenu(Color.parseColor("#30A400"), R.mipmap.icon_notify)
                .addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.icon_setting)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {

                        if (index == 1) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i = new Intent(MainActivity.this, Edit.class);
                                    startActivity(i);
                                }
                            }, 1000);
                        }


                        if (index == 2) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i = new Intent(MainActivity.this, CriticalLevel.class);
                                    startActivity(i);
                                }
                            }, 1000);
                        }
                    }

                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {


            @Override
            public void onMenuOpened() {
            }

            @Override
            public void onMenuClosed() {
            }

        });


    }


    //Create Broadcast Receiver Object along with class definition
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        //When Event is published, onReceive method is called
        public void onReceive(Context c, Intent i) {
            //Get Battery %
            int level = i.getIntExtra("level", 0);
            //Find the progressbar creating in main.xml

            //Find textview control created in main.xml
            TextView tv = (TextView) findViewById(R.id.textfield);
            //Set TextView with text
            tv.setText(Integer.toString(level) + "%");


        }

    };


//    Circle menu methods

    public boolean onMenuOpened(int featureId, Menu menu) {
        circleMenu.openMenu();
        return super.onMenuOpened(featureId, menu);
    }

    public void onBackPressed() {
        circleMenu.closeMenu();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);

    }


}






