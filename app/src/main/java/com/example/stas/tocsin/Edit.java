package com.example.stas.tocsin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.gsm.SmsManager;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by stas on 11/09/16.
 */
public class Edit extends Activity {

    private static final int PICK_CONTACT = 1;
    EditText mEdit, msg;
    String number, contact, critlevel;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

//        view elements
        ImageButton submit = (ImageButton) findViewById(R.id.submit);
        ImageButton checkSms = (ImageButton) findViewById(R.id.checkSms);

        mEdit = (EditText) findViewById(R.id.select_contact);
        msg = (EditText) findViewById(R.id.msg);
//        don't display keyboard
        mEdit.setInputType(InputType.TYPE_NULL);

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String userName = prefs.getString("contact", null);
        String userPhone = prefs.getString("phone", null);
        String messAge = prefs.getString("msg", null);
        String critLevel = prefs.getString("defaultLevel", null);

        number = userPhone;
        contact = userName;
        critlevel = critLevel;
        mEdit.setText(userName + " " + userPhone);
        msg.setText(messAge);

        checkSms.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View v) {
                String phone = number;
                String sms = msg.getText().toString();
                if (msg.getText().toString() != null) {

                    reverseButtonAnimation(v);
                    sendSMS(phone, sms);
                }

            }
        });

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
                        editor.putString("defaultLevel", critlevel);
                        editor.commit();
//                        intent to another activity
                        if (number != null && !msg.getText().toString().isEmpty()) {

                            buttonAnimation(view);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(Edit.this, MainActivity.class);
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

    //Get Contact Phone Number
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {


                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                //Get Phone Number
                number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contact = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number = number.replace("-", "");
                mEdit.setText(contact + "  " + number);


            }
        }
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), " Message Sent  ",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {

            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void onBackPressed() {

    }

    public void buttonAnimation(View view) {

        ImageButton submit = (ImageButton) findViewById(R.id.submit);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        submit.startAnimation(myAnim);
    }


    public void reverseButtonAnimation(View view) {

        ImageButton submit = (ImageButton) findViewById(R.id.checkSms);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.reversed_bounce);
        submit.startAnimation(myAnim);
    }

}



