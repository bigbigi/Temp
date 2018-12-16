package com.reomote.carcontroller.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by big on 2018/12/15.
 */

public class Connector {
    private static final String TAG = "Connector";//服务器端ip地址

    private Socket mSocket;

    public Connector(String ip,int port) {
        try {
            mSocket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
        try {
            Log.d(TAG, "send:" + data);
            mSocket.getOutputStream().write(hexStrToBinaryStr(data));
            mSocket.getOutputStream().write(hexStrToBinaryStr(data));
        } catch (Exception e) {
            Log.d(TAG, "send Exception:" + data);
            e.printStackTrace();
        }
    }

    public static void receive(int port) {
        Socket socket = null;
        DataInputStream dis = null;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            dis = new DataInputStream(socket.getInputStream());
            byte[] body = new byte[1];
            while (dis.read(body) > 0) {
                Log.d(TAG, "message:" + Integer.toHexString(body[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeIO(dis);
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void close() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
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

//    public static int byte2Int(byte[] bytes) {
//        return (bytes[0] & 0xff) << 24
//                | (bytes[1] & 0xff) << 16
//                | (bytes[2] & 0xff) << 8
//                | (bytes[3] & 0xff);
//    }
}
