package com.reomote.carcontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.autofit.widget.TextView;

import org.videolan.VlcPlayer;


public class MainActivity extends Activity {
    VlcPlayer mPlayer;
    private TextView mDelayText;
    private TextView mSpeedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDelayText = (TextView) findViewById(R.id.netdelay);
        mSpeedText = (TextView) findViewById(R.id.netspeed);
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
        Log.d("big", "screen:" + getResources().getDisplayMetrics().widthPixels + "," + getResources().getDisplayMetrics().heightPixels);
        mPlayer = (VlcPlayer) findViewById(R.id.player);
        mHandler.sendEmptyMessage(MSG_SPEED);
    }


    private static final int MSG_SPEED = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SPEED:
                    getSpeed();
                    sendEmptyMessageDelayed(MSG_SPEED, 3000);
                    break;
            }
        }
    };
    long mLastTotalBytes;
    long mLastTimeStamp;

    private void getSpeed() {
        long nowTotalBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = (getTotalRxBytes() - mLastTotalBytes) * 1000 / (nowTimeStamp - mLastTimeStamp);
        mLastTimeStamp = nowTimeStamp;
        mLastTotalBytes = nowTotalBytes;
        mSpeedText.setText(speed + "KB/S");
    }

    public long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(Process.myUid()) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    @Override
    protected void onResume() {
        mPlayer.setVideoPath("rtsp://13728735758:abcd1234@10.2.0.76:554/stream1");
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayer.stop();
    }


    @Override
    public void finish() {
        super.finish();
        mPlayer.destroy();
        System.exit(0);
    }
}
