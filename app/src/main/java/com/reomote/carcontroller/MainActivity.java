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

import com.autofit.widget.ScreenParameter;
import com.autofit.widget.TextView;
import com.reomote.carcontroller.widget.VideoView;


public class MainActivity extends Activity {
    VideoView mPlayer;
    private TextView mTitle;
    private TextView mDelayText;
    private TextView mSpeedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDelayText = (TextView) findViewById(R.id.netdelay);
        mSpeedText = (TextView) findViewById(R.id.netspeed);
        mTitle= (TextView) findViewById(R.id.title);
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
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int dw = display.getWidth();
        int dh = display.getHeight();
        Log.d("big", "screen:" + dw + "," + dh);
        mPlayer = (VideoView) findViewById(R.id.player);
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
        mSpeedText.setText(speed + "KB");
    }

    public long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(Process.myUid()) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.setVideoPath("rtsp://13728735758:abcd1234@10.2.0.76:554/stream1");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
    }

}
