package com.smsapp.helpme;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by gamelooper on 3/30/2018.
 * #format
 * helpme <action> password</>
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG = "SmsBroadcastReceiver";
    DatabaseHelper databaseHelper;
    private String fromSender = "";
    static final int RESULT_ENABLE = 1;


    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        }
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsSender += smsMessage.getDisplayOriginatingAddress();
                smsBody += smsMessage.getMessageBody();
            }

            if (smsBody.startsWith("helpme") || smsBody.startsWith("Helpme")) {

                String[] tokens = smsBody.split(" ");

                databaseHelper = new DatabaseHelper(context);
                Cursor cursor = databaseHelper.getDefaultRecord();
                cursor.moveToFirst();
                String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL2));
                fromSender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL1));
                try {
                    String checkpswd = tokens[2].trim();
                    if (tokens[1].trim().equals("contact"))
                        checkpswd = tokens[3].trim();
                    if (null == tokens[2] || !checkpswd.equals(password))
                        return;
                } catch (Exception e) {
                    Log.d(TAG, " wrong SMS  format detected: From " + smsSender + " With text " + smsBody);
                    return;
                }

                switch (tokens[1].trim()) {
                    case "ring":
                        Log.d(TAG, "Sms with condition detected");
                        Toast.makeText(context, "BroadcastReceiver caught conditional SMS: " + smsBody, Toast.LENGTH_LONG).show();
                        Intent startRingActivity = new Intent(context, RingtonePlayingService.class);
                        context.startService(startRingActivity);

                        Intent stopRingActivity = new Intent(context, StopRingActivity.class);
                        stopRingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(stopRingActivity);

                        break;
                    case "location":
                        String location = getLocation(context);
                        sendMessage(location, smsSender);
                        break;

                    case "contact":
                        String number = getContactName(tokens[2].trim(), context);
                        sendMessage(number, smsSender);
                        break;

                    case "lock":


                        Intent startLockActivity = new Intent(context, LockActivity.class);
                        startLockActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startLockActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startLockActivity);



                        break;


                }

            }
            Log.d(TAG, "SMS detected: From " + smsSender + " With text " + smsBody);
        }
    }


    public String getLocation(Context context) throws SecurityException {
        String defaultLoc= "Gnana Bharathi, Mallathahalli, Bengaluru, Karnataka 560056";
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List addresses = new ArrayList();
        String locationProvider;

        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        }


        Location loc = locationManager.getLastKnownLocation(locationProvider);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        System.out.println("1::" + loc);
        String addr = "";
            if(null==loc)
                return defaultLoc;
        try {
            Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            addresses = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses.isEmpty()) {
                System.out.println("Waiting for Location");
            } else {
                if (addresses.size() > 0) {
                    Address m = (Address) addresses.get(0);
                    addr = m.getAddressLine(0);
                    System.out.println("adddreess " + addr);


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addr=defaultLoc;
        }
        return addr;
    }

    public String getContactName(final String name, Context context) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if (ret == null)
            ret = "Unsaved";
        System.out.println("contact number " + ret);
        return ret;
    }

    private void sendMessage(String text, String smsSender) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsSender, null, text, null, null);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}


