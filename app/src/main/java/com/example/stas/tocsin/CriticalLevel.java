package com.example.stas.tocsin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by stas on 11/09/16.
 */

public class CriticalLevel extends Activity implements SeekBar.OnSeekBarChangeListener {

    TextView tv;
    int level = 3;
    int current = 0;
    public static final String MY_PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critical_level);

        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        tv = (TextView) findViewById(R.id.precent);
        tv.setText("Critical Level is");


        SeekBar batteryLevelBar = (SeekBar) findViewById(R.id.seekBar);
        batteryLevelBar.setOnSeekBarChangeListener(this);

        ImageButton submit = (ImageButton) findViewById(R.id.submit);
        submit.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("defaultLevel", String.valueOf(level));
                        editor.commit();
                        buttonAnimation(view);
//                      delay handler
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent i = new Intent(CriticalLevel.this, MainActivity.class);
                                startActivity(i);
                            }
                        }, 1200);

                    }
                });


    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {


        if (progress < current) {
            if (progress <= 3) {
                progress = 3;
                level = progress;
            }
            if (progress >= 80) {
                progress = 80;
                level = progress;
            }
            tv.setText("Critical Level is  " + Integer.toString(progress) + "%");

            level = progress;

        } else {
            Toast.makeText(CriticalLevel.this, " Please Select Level lower than " + current + "%", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        //When Event is published, onReceive method is called
        public void onReceive(Context c, Intent i) {

            int Currentlevel = i.getIntExtra("level", 0);
            current = Currentlevel;
        }

    };


    public void buttonAnimation(View view) {
        ImageButton submit = (ImageButton) findViewById(R.id.submit);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        submit.startAnimation(myAnim);
    }
}

