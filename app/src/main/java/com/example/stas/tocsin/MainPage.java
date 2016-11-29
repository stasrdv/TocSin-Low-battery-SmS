package com.example.stas.tocsin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by stas on 12/09/16.
 * <p>
 * This is my "Welcome" page,we see it only once after the install
 */
public class MainPage extends Activity {


    EditText mEdit, msg;
    String number, contact;
    String defaultLevel = "3";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private static final int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

/**
 *Beginning in Android 6.0 (API level 23),
 * users grant permissions to apps while the app is running,
 * not when they install the app.
 */
        if (Build.VERSION.SDK_INT >= 6.0) {
            askForPermission(Manifest.permission.SEND_SMS, 1);
        }


        SharedPreferences preferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String phone = preferences.getString("phone", null);
        if (phone != null) {
            Intent i = new Intent(MainPage.this, MainActivity.class);
            startActivity(i);
        }


//      Declare View elements
        ImageButton submit = (ImageButton) findViewById(R.id.submit);
        mEdit = (EditText) findViewById(R.id.select_contact);
        msg = (EditText) findViewById(R.id.msg);
        mEdit.setInputType(InputType.TYPE_NULL);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }


        });

//      Go to next activity
        submit.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("phone", number);
                        editor.putString("msg", msg.getText().toString());
                        editor.putString("contact", contact);
                        editor.putString("defaultLevel", defaultLevel);
                        editor.commit();

                        if (number != null && !msg.getText().toString().isEmpty()) {
                            animateButton(view);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainPage.this, "Welcome To  Tocsin ! ", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(MainPage.this, MainActivity.class);
                                    startActivity(i);
                                }
                            }, 1200);


                        } else if (number == null) {
                            Toast.makeText(getApplicationContext(), " Please select contact  ",
                                    Toast.LENGTH_LONG).show();
                        } else if (msg.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), " The message is empty  ",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    //Get user's phone number method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {

                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();
                number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contact = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number = number.replace("-", "");
                mEdit.setText(contact + "  " + number);

            }
        }
    }

    //Ask for permissions during RunTime
    private void askForPermission(String permission, Integer requestCode) {

        if (ContextCompat.checkSelfPermission(MainPage.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainPage.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainPage.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainPage.this, new String[]{permission}, requestCode);


            }
        }
    }


    //Check if user denied the permission
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(MainPage.this, "Permission denied ! ", Toast.LENGTH_SHORT).show();

                    finish();
                    System.exit(0);

                }
                return;
            }

        }
    }

    // Block the back Key
    public void onBackPressed() {
        Toast.makeText(MainPage.this, "Please fill all required fields  ! ", Toast.LENGTH_SHORT).show();

    }

    public void animateButton(View view) {
        ImageButton submit = (ImageButton) findViewById(R.id.submit);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        submit.startAnimation(myAnim);
    }


}

