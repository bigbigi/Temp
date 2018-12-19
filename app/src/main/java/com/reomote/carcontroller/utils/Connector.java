package com.reomote.carcontroller.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by big on 2018/12/15.
 */

public class Connector {
    private static final String TAG = "Connector";//服务器端ip地址
    private String mIp;
    private int mPort;
    private Context mContext;

    private Socket mSocket;

    public Connector(Context context, String ip, int port) {
        mContext = context;
        mIp = ip;
        mPort = port;
        createSocket();
    }

    public void send(String data) {
        try {
            Log.d(TAG, "send:" + data + ",connect:" + mSocket.isConnected() + "ip:" + mIp + ",prot:" + mPort);
            mSocket.getOutputStream().write(hexStrToBinaryStr(data));
            mSocket.getOutputStream().write(hexStrToBinaryStr(data));
        } catch (Exception e) {
            Log.d(TAG, "send Exception:" + data);
            close();
            ThreadManager.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "断开连接", Toast.LENGTH_LONG).show();
                }
            });
            createSocket();
            e.printStackTrace();
        }
    }

    private void createSocket() {
        while (mSocket == null || mSocket.isClosed()) {
            try {
                mSocket = new Socket(mIp, mPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ThreadManager.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "已连上小车", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isConnect() {
        return mSocket != null && !mSocket.isClosed();
    }

    public static void receive(int port) {
        Socket socket = null;
        DataInputStream dis = null;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                dis = new DataInputStream(socket.getInputStream());
                byte[] body = new byte[1];
                while (dis.read(body) > 0) {
                    Log.d(TAG, "message:" + Integer.toHexString(body[0]));
                }
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
        Log.e(TAG, "receive close");
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
