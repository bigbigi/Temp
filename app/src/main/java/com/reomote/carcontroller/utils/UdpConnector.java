package com.reomote.carcontroller.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by big on 2018/12/15.
 */

public class UdpConnector {
    private static final String TAG = "Connector";//服务器端ip地址
    private String mIp;
    private int mPort;
    private InetAddress mInetAddress;
    private Context mContext;

    private DatagramSocket mSocket;

    public UdpConnector(Context context, String ip, int port) {
        mContext = context;
        mIp = ip;
        mPort = port;
        try {
            mSocket = new DatagramSocket(mPort);
            mSocket.setSoTimeout(3000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            mInetAddress = InetAddress.getByName(ip);
            ThreadManager.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "已建立连接", Toast.LENGTH_LONG).show();
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public void send(String data) {
        try {
            byte[] bytes = hexStrToBinaryStr(data);
            mSocket.send(new DatagramPacket(bytes, bytes.length, mInetAddress, mPort));
            mSocket.send(new DatagramPacket(bytes, bytes.length, mInetAddress, mPort));
            Log.d(TAG, "send:" + data + ",length:" + bytes.length);
        } catch (Exception e) {
            Log.d(TAG, "send Exception:" + data);
            e.printStackTrace();
        }
    }


    public static void receive(int port) {
        Log.e(TAG, "receive start:" + port);
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] singleBuff = new byte[10];
            DatagramPacket packet = new DatagramPacket(singleBuff, singleBuff.length);
            while (true) {
                serverSocket.receive(packet);
                byte[] data = packet.getData();
                Log.d(TAG, "data:" + data.length);
                for (byte value : data) {
                    Log.d(TAG, "message:" + Integer.toHexString(value));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "receive close");
    }

    public void close() {
        if (mSocket != null) {
            mSocket.close();
        }
    }

    /**
     * 将十六进制的字符串转换成字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStrToBinaryStr(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        int index = 0;
        byte[] bytes = new byte[len / 2];

        while (index < len) {
            String sub = hexString.substring(index, index + 2);
            bytes[index / 2] = (byte) Integer.parseInt(sub, 16);
            index += 2;
        }
        return bytes;
    }

}
