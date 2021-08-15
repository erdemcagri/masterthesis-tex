package com.example.appbq_none;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences(this.getFilesDir().getName(), MODE_PRIVATE);

    }

    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        if (getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            preferencesEditor.putString("CONNECTIVITY", "Connection:" + checkWifiConnection());
            preferencesEditor.putString("WIFI-INFO", wifiInfo());
            preferencesEditor.putString("CONFIGURED-NETWORKS", prevConnNetworks());
            preferencesEditor.putString("WIFI-SCAN", getWifiScan());
            preferencesEditor.apply();
        }
        if ((getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED) &&
                (getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED)) {
            preferencesEditor.putString("BT-MAC-ADDR", getBtInfo());
            preferencesEditor.putString("PAIRED-BT", getPairedBt());
            preferencesEditor.putString("BT-DISCOVERY", btDiscoverDevices());
            //preferencesEditor.putString("BT-CONNECTED-DEVICE", getConnBtDevice());
            preferencesEditor.apply();
        }

        preferencesEditor.putString("SECURE-ANDROID-ID", getSecureId());
        preferencesEditor.apply();
    }

    private boolean checkWifiConnection() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            return true; // Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
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
                        "Address: " + btDevice.getAddress() +
                        "Contents: " + btDevice.describeContents() +
                        "Class: " + btDevice.getBluetoothClass() +
                        "Type: " + btDevice.getType() +
                        "UUIDs: " + btDevice.getUuids() + "---");
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(btBroadcastReceiver);
        super.onDestroy();
    }

    private String btDiscoverDevices() {
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager)
                this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btBroadcastReceiver, filter);

        return btDeviceList.toString();
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

    private String getSecureId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getNfcInfo(intent);
    }

    String nfcInfo = null;
    private void getNfcInfo(Intent intent) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        Tag nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcInfo = nfcTag.toString();
        if (nfcInfo != null) {
            Toast.makeText( this, nfcInfo + "-- Info saved to file.", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putString("NFC-INFO", nfcInfo+"info");
        preferencesEditor.apply();
    }

}
