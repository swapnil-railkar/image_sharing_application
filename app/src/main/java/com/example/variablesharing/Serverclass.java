package com.example.variablesharing;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Serverclass extends Thread {

    Socket socket;
    ServerSocket serverSocket;
    OutputStream outputStream;
    Bitmap bitmap;
    Boolean flag;
    Serverclass(Bitmap bitmap) {
        this.bitmap = bitmap;
        new Thread(this,"Server").start();
    }


    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
            outputStream = socket.getOutputStream();

            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }


    }


}
