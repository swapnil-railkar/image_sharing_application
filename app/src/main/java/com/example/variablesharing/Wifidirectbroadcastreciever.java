package com.example.variablesharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class Wifidirectbroadcastreciever extends BroadcastReceiver {
    public WifiP2pManager mmanager;
    public WifiP2pManager.Channel mchannel;
    public SharingScreen mactivity;

    public Wifidirectbroadcastreciever(WifiP2pManager mmanager, WifiP2pManager.Channel mchannel, SharingScreen mactivity) {
        this.mmanager = mmanager;
        this.mchannel = mchannel;
        this.mactivity = mactivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "Wifi is on", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Wifi is off", Toast.LENGTH_LONG).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mmanager != null) {


                if (ActivityCompat.checkSelfPermission(mactivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(mactivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    return;
                }
                mmanager.requestPeers(mchannel, mactivity.peerListListener);
           }
      }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
      {

           if(mmanager==null)
           {
               return;
           }
          NetworkInfo networkInfo=intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
           if(networkInfo.isConnected()) {
               mmanager.requestConnectionInfo(mchannel, mactivity.connectionInfoListener);

           }



      }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
      {

      }
    }
}
