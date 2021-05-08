package com.example.variablesharing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SharingScreen extends AppCompatActivity {


    int image_pick_code = 1000;

    WifiP2pManager mmanager;
    WifiP2pManager.Channel mchannel;
    BroadcastReceiver mreciever;
    IntentFilter mintentfilter;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] devicenamearray;
    WifiP2pDevice[] devicearray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sharingscreen);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initialwork();

    }


    private void initialwork() {
        mmanager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mchannel = mmanager.initialize(this, getMainLooper(), null);
        mreciever = new Wifidirectbroadcastreciever(mmanager, mchannel, this);
        mintentfilter = new IntentFilter();
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mintentfilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerlist) {
            if (!peerlist.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerlist.getDeviceList());
                devicenamearray = new String[peerlist.getDeviceList().size()];
                devicearray = new WifiP2pDevice[peerlist.getDeviceList().size()];
                int index = 0;
                for (WifiP2pDevice device : peerlist.getDeviceList()) {
                    devicenamearray[index] = device.deviceName;
                    devicearray[index] = device;
                    index++;
                }
                ListView listView = (ListView) findViewById(R.id.listview);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, devicenamearray);
                listView.setAdapter(adapter);
            }
            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No devices available", Toast.LENGTH_SHORT).show();
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            TextView connectionstat = (TextView) findViewById(R.id.connectionstat2);

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionstat.setText("Host");
                pickImageFromGallery();
                // data transfer will take place from host to client
            } else if (wifiP2pInfo.groupFormed) {
                connectionstat.setText("Client");
                new Clientclass(groupOwnerAddress);


            }
        }
    };

    // method to handle contents received from input stream
    public Handler handle = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message msg) {

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            Bitmap bitmap;
            byte[] readBuff = (byte[]) msg.obj;
                //covert byte array to bitmap
                bitmap = BitmapFactory.decodeByteArray(readBuff, 0, readBuff.length);

                //covert bitmap to drawable and set image view
                Drawable drawable = new BitmapDrawable(getApplicationContext().getResources(),
                        bitmap);
                imageView.setImageDrawable(drawable);

                Toast.makeText(getApplicationContext(), "Image received",
                        Toast.LENGTH_LONG).show();

            return true;

        }
    });


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mreciever, mintentfilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mreciever);
    }


    public void onclickDiscover(View view) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mmanager.discoverPeers(mchannel, new WifiP2pManager.ActionListener() {

            TextView connectionstat = (TextView) findViewById(R.id.connectionstat2);

            @Override
            public void onSuccess() {
                connectionstat.setText("Discovery Started...");
            }

            @Override
            public void onFailure(int i) {
                connectionstat.setText("Discovery Starting Failed...");
            }
        });
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = devicearray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                if (ActivityCompat.checkSelfPermission(SharingScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(SharingScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                mmanager.connect(mchannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to :" + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Not Connected...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    // method to pick an image from device gallery
    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, image_pick_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED)
                    pickImageFromGallery();
                else Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
            break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == image_pick_code) {
            ImageView img = findViewById(R.id.imageView);
            TextView pathtext = findViewById(R.id.textView);
            try {
                Uri imgpath = data.getData();
                img.setImageURI(imgpath);
                String path = Objects.requireNonNull(data.getData()).getPath();
                pathtext.setText("Path: " + path);


                // method to covert image to bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgpath);

                // starting Server class thread
                new Serverclass(bitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //client class
    public class Clientclass extends Thread {
        Socket socket;
        String hostadd;
        InputStream inputStream;

        public Clientclass(InetAddress hostaddress) {
            hostadd = hostaddress.getHostAddress();
            socket = new Socket();
            new Thread(this,"Client").start();
        }

        public void run() {
            byte[] buffer = new byte[5000000];
            int bytes;
            try {
                socket.connect((new InetSocketAddress(hostadd, 8888)), 500);
                inputStream = socket.getInputStream();
                if (socket != null)
                {
                        Clientclass.sleep(10000); 
                        bytes = inputStream.read(buffer);
                        if (bytes > 0 ){
                            handle.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                        }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}