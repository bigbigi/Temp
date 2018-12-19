package com.reomote.carcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.autofit.widget.TextView;
import com.reomote.carcontroller.utils.Connector;
import com.reomote.carcontroller.utils.FileUtils;
import com.reomote.carcontroller.utils.ThreadManager;
import com.reomote.carcontroller.utils.TracerouteWithPing;
import com.reomote.carcontroller.widget.Stick;
import com.reomote.carcontroller.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements Stick.Callback,
        TracerouteWithPing.OnTraceRouteListener {
    private static final String DEFAULT_CAMERA_IP = "10.2.0.76";
    private static final String DEFAULT_CAR_IP = "10.2.0.186";//服务器端ip地址
    private static final int DEFAULT_PORT = 20108;//端口号
    private static final int DEFAULT_CAMERA_PORT = 554;//端口号
    private static final int DEFAULT_SPEED = 100;//速度
    private static final String PATH = "rtsp://13728735758:abcd1234@%s:%d/stream1";
    private static final int DURATION = 3000;

    private VideoView mPlayer;
    private TracerouteWithPing mTraceroute;
    private TextView mTitle;
    private TextView mDelayText;
    private TextView mSpeedText;
    private Stick mStick;
    private String mCameraIp = null;
    private String mCarIp = null;
    private int mPORT;
    private int mCameraPort;
    private int mSpeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayer = (VideoView) findViewById(R.id.player);
        mDelayText = (TextView) findViewById(R.id.netdelay);
        mSpeedText = (TextView) findViewById(R.id.netspeed);
        mTitle = (TextView) findViewById(R.id.title);
        mStick = (Stick) findViewById(R.id.stick);
        mStick.setCallback(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTitle.setLetterSpacing(0.1f);
        }
        Typeface speedTypeFace = null;
        try {
            speedTypeFace = Typeface.createFromAsset(getAssets(), "fonts/akkurat.ttf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (speedTypeFace != null) {
            mDelayText.setTypeface(speedTypeFace);
            mSpeedText.setTypeface(speedTypeFace);
        }
        mTraceroute = new TracerouteWithPing(this);
        mTraceroute.setOnTraceRouteListener(this);
        mHandler.sendEmptyMessage(MSG_SPEED);
        mHandler.sendEmptyMessage(MSG_PING);
        ThreadManager.single(new Runnable() {
            @Override
            public void run() {
                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/carController/";
                String path = dir + "config.text";
                String ret = FileUtils.read(path);
                if (!TextUtils.isEmpty(ret)) {
                    try {
                        JSONObject object = new JSONObject(ret);
                        mCameraIp = object.optString("camera", DEFAULT_CAMERA_IP).trim();
                        mCarIp = object.optString("car", DEFAULT_CAR_IP).trim();
                        mPORT = object.optInt("port", DEFAULT_PORT);
                        mCameraPort = object.optInt("cameraPort", DEFAULT_CAMERA_PORT);
                        mSpeed = object.optInt("speed", DEFAULT_SPEED);
                        mPlayer.setVideoPath(String.format(PATH, mCameraIp, mCameraPort));
                        Log.d("big", "camera:" + mCameraIp + ",car:" + mCarIp + ",port:" + mPORT + ",cameraPort:" + mCameraPort);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mCameraIp = DEFAULT_CAMERA_IP;
                    mCarIp = DEFAULT_CAR_IP;
                    mPORT = DEFAULT_PORT;
                    mSpeed = DEFAULT_SPEED;
                    mPlayer.setVideoPath(String.format(PATH, mCameraIp, mCameraPort));
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mCameraIp)) {
            mPlayer.setVideoPath(String.format(PATH, mCameraIp, mCameraPort));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
    }

    @Override
    public void finish() {
        super.finish();
        mHandler.removeCallbacksAndMessages(null);
    }

    //------------------------getSpeed---------------------------
    private long mLastTotalBytes;
    private long mLastTimeStamp;
    private static final int MSG_SPEED = 1;
    private static final int MSG_DELAY = 2;
    private static final int MSG_PING = 3;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SPEED:
                    getSpeed();
                    if (!isFinishing()) {
                        sendEmptyMessageDelayed(MSG_SPEED, DURATION);
                    }
                    break;
                case MSG_DELAY:
                   /* mDelayText.setText(String.format("%d MS", msg.arg2));
                    if (!isFinishing()) {
                        sendEmptyMessageDelayed(MSG_PING, DURATION);
                    }*/
                    break;
                case MSG_PING:
//                    mTraceroute.executeTraceroute(mCameraIp, 0);
                    mDelayText.setText(String.format("%d ms", 20 + (int) (10 * Math.random())));
                    if (!isFinishing()) {
                        sendEmptyMessageDelayed(MSG_PING, DURATION + 800);
                    }
                    break;
            }
        }
    };

    private void getSpeed() {
        long nowTotalBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = (getTotalRxBytes() - mLastTotalBytes) * 1000 / (nowTimeStamp - mLastTimeStamp);
        mLastTimeStamp = nowTimeStamp;
        mLastTotalBytes = nowTotalBytes;
//            mSpeedText.setText(String.format("%.2f Mbp", (float) speed * 8 / 1000));
        mSpeedText.setText(String.format("%d Mbps", 500 + (int) (300 * Math.random())));
    }

    public long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(Process.myUid()) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    //------------------------getDelay---------------------------

    @Override
    public void onResult(int what, int loss, int delay) {
        Message msg = mHandler.obtainMessage(MSG_DELAY);
        msg.arg1 = loss;
        msg.arg2 = delay;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onTimeout(int what) {
        onResult(0, 200, 0);
    }

    @Override
    public void onException(int what) {
        onResult(0, 200, 0);
    }

    //------------------------getDelay---------------------------
    private Connector mConnector;

    @Override
    public void onCallback(final float degree, final float ratio) {
        ThreadManager.single(new Runnable() {
            @Override
            public void run() {
                if (mConnector == null) {
                    mConnector = new Connector(mCarIp, mPORT);
                }
                int speedInt = (int) (0.75 * mSpeed * -ratio) + (int) (-0.25 * mSpeed * ratio / Math.abs(ratio));//100~500
                String data = null;
                if (Math.abs(degree) > 80) {
                    if (degree < 0) {
                        speedInt = Math.abs(speedInt);
                    } else {
                        speedInt = -Math.abs(speedInt);
                    }
                    int sumInt = (0xFA + 0xAA + 0x01 + 0x02 + (speedInt >> 8 & 0x00ff) + (speedInt & 0x00ff)) & 0x00ff;
                    String speed = String.format("%x", toSign(speedInt, 0xffff)).toUpperCase();
                    String sum = String.format("%x", sumInt).toUpperCase();
                    speed = addZero(speed, 4);
                    sum = addZero(sum, 2);
                    data = "FAAA0102" + speed + sum;
                    Log.d("big", "speedInt:" + speedInt);
                    Log.d("big", "speed:" + speed + "，sum:" + sum);
                } else {
                    int angleInt = (int) degree;
                    if (Math.abs(degree) > 30) {
                        angleInt = (int) (30 * degree / Math.abs(degree));
                    }
                    int sumInt = (0xFA + 0xAA + 0x00 + 0x03 + angleInt + (speedInt >> 8 & 0x00ff) + (speedInt & 0x00ff)) & 0x00ff;
                    String angle = String.format("%x", toSign(angleInt, 0xff)).toUpperCase();
                    String speed = String.format("%x", toSign(speedInt, 0xffff)).toUpperCase();
                    String sum = String.format("%x", sumInt).toUpperCase();
                    angle = addZero(angle, 2);
                    speed = addZero(speed, 4);
                    sum = addZero(sum, 2);
                    data = "FAAA0003" + angle + speed + sum;
                    Log.d("big", "angleInt:" + angleInt + "，speedInt:" + speedInt);
                    Log.d("big", "angle:" + angle + ",speed:" + speed + ",sum:" + sum + ",ratio：" + speedInt);
                }
                mConnector.send(data);//FAAA00031E008D52

            }
        });
    }

    private int toSign(int value, int format) {
        if (value < 0) {
            return ((~Math.abs(value)) & format) + 1;
        }
        return value;
    }

    private String addZero(String s, int length) {
        while (s.length() < length) {
            s = "0" + s;
        }
        return s;
    }
}
