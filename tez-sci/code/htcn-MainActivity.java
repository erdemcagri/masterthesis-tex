package com.example.apphtc_none;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

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
        //preferencesEditor.putString("WIFI-INFO", wifiInfo());
        //preferencesEditor.putString("CONFIGURED-NETWORKS", prevConnNetworks());
        //preferencesEditor.putString("WIFI-SCAN", getWifiScan());
        //preferencesEditor.putString("BT-MAC-ADDR", getBtInfo());
        //preferencesEditor.putString("PAIRED-BT", getPairedBt());
        //preferencesEditor.putString("BT-DISCOVERY", btDiscoverDevices());
        //preferencesEditor.putString("BT-CONNECTED-DEVICE", getConnBtDevice());
        preferencesEditor.putString("SECURE-ANDROID-ID", getSecureId());
        preferencesEditor.putString("BUILD-INFO", getBuildInfo());
        preferencesEditor.apply();

    }

    /*private String wifiInfo() {
        WifiManager wifiManager = (WifiManager)
                this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String wifiMac = "Device Wi-Fi Mac address: " + wifiManager.getConnectionInfo().getMacAddress();
        String connectedAp = " - Connected Wi-Fi: " + wifiManager.getConnectionInfo().toString();
        return wifiMac + connectedAp;
    }*/

    /*private String prevConnNetworks() {
        WifiManager wifiManager = (WifiManager)
                this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredList = wifiManager.getConfiguredNetworks();
        return configuredList.toString();
    }*/

    /*private String getWifiScan() {
        WifiManager wifiManager = (WifiManager)
                this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        return scanResults.toString();
    }*/

    /*private String getConnBtDevice() {
        BluetoothManager bluetoothManager = (BluetoothManager)
                this.getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
        List<BluetoothDevice> btDevice = bluetoothManager.getConnectedDevices(GATT_SERVER);

        return btDevice.toString();
    }*/

    /*private String getBtInfo() {
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
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btBroadcastReceiver, filter);

        return btDeviceList.toString();
    }*/

    /*private String getPairedBt() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> btDeviceList = bluetoothAdapter.getBondedDevices();

        List<String> list = new ArrayList<>();
        for(BluetoothDevice bluetoothDevice : btDeviceList) {
            list.add("--- Name: " + bluetoothDevice.getName());
            list.add("Address: " + bluetoothDevice.getAddress());
            list.add("Contents: " + bluetoothDevice.describeContents());
            list.add("Class: " + bluetoothDevice.getBluetoothClass());
            list.add("UUIDs: " + bluetoothDevice.getUuids() + "---");
        }
        return list.toString();
    }*/

    private String getSecureId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
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