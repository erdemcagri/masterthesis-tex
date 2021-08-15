package com.example.apphtc_full;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logToFile(this, "test line");
        logToFile(this, readSms());
        receiveSms();
        logToFile(this, getContacts());
        logToFile(this, getCallLog());
        logToFile(this, getProfile());
        logToFile(this, getCalendar());
        logToFile(this, getDictionary());
        logToFile(this, getLoc());
        logToFile(this, wifiInfo());
        logToFile(this, prevConnNetworks());
        logToFile(this, getWifiScan());
        logToFile(this, getBtInfo());
        logToFile(this, getPairedBt());
        logToFile(this, getAcc());
        logToFile(this, getTelephonyInfo());
        logToFile(this, getBuildInfo());
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


    private String getContacts() {
        List<String> contactList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((c != null ? c.getCount() : 0) > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactList.add(name);
                if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
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
        if (c != null) {
            c.close();
        }
        return contactList.toString();
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

    private String getDictionary() {
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(UserDictionary.Words.CONTENT_URI, null, null, null, null);
        List<String> words = new ArrayList<>();
        if ((c != null ? c.getCount() : 0) > 0) {
            while (c.moveToNext()) {
                words.add(c.getString(c.getColumnIndex(UserDictionary.Words.WORD)) +
                          " / " + c.getString(c.getColumnIndex(UserDictionary.Words.FREQUENCY)));
            }
        }
        else {
            words.add("\nNo words found in the user dictionary!\n");
        }
        assert c != null; c.close();
        return words.toString();
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

    private String getBtInfo() {
        return "\nBluetooth MAC Address: " + BluetoothAdapter.getDefaultAdapter().getAddress() + "\n";
    }

    private String getPairedBt() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        List<String> list = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            list.add("--- Name: " + bluetoothDevice.getName());
            list.add("Address: " + bluetoothDevice.getAddress());
            list.add("Contents: " + bluetoothDevice.describeContents());
            list.add("Class: " + bluetoothDevice.getBluetoothClass());
            list.add("UUIDs: " + bluetoothDevice.getUuids() + "---");


        }
        return list.toString();
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
                        " UUIDs: " + btDevice.getUuids() + "---");
            }
            logToFile(getApplicationContext(), btDeviceList.toString());
            logToFile(getApplicationContext(), "\nDiscovered bt devices listed.\n");
        }
    };

    private void btDiscoverDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btBroadcastReceiver, filter);
    }

    private String getAcc() {
        List<String> accountList = new ArrayList<>();
        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                accountList.add("Account e-mail: " + account.name+"\n");
            }
        }
        return accountList.toString();
    }

    @SuppressLint("MissingPermission")
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


}