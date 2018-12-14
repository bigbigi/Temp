package com.reomote.carcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.autofit.widget.TextView;
import com.reomote.carcontroller.utils.TracerouteWithPing;
import com.reomote.carcontroller.widget.Stick;
import com.reomote.carcontroller.widget.VideoView;


public class MainActivity extends Activity implements Stick.Callback,
        TracerouteWithPing.OnTraceRouteListener {
    private static final String IP = "10.2.0.76";
    private static final String PATH = "rtsp://13728735758:abcd1234@10.2.0.76:554/stream1";
    private static final int DURATION = 3000;

    private VideoView mPlayer;
    private TracerouteWithPing mTraceroute;
    private TextView mTitle;
    private TextView mDelayText;
    private TextView mSpeedText;
    private Stick mStick;


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
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.setVideoPath(PATH);
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
                    mDelayText.setText(String.format("%d MS", msg.arg2));
                    if (!isFinishing()) {
                        sendEmptyMessageDelayed(MSG_PING, DURATION);
                    }
                    break;
                case MSG_PING:
                    mTraceroute.executeTraceroute(IP, 0);
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
        mSpeedText.setText(String.format("%.2f Mbp", (float) speed * 8 / 1000));
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

    }

    @Override
    public void onException(int what) {

    }

    //------------------------getDelay---------------------------
    @Override
    public void onCallback(float degree, float ratioX, float ratioY) {
        Log.d("big", "degree:" + degree + ",ratioX:" + ratioX + ",ratioY:" + ratioY);
    }
}
