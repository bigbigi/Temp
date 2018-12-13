package com.reomote.carcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.videolan.VlcPlayer;


public class MainActivity extends Activity {
    VlcPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("big", "screen:" + getResources().getDisplayMetrics().widthPixels + "," + getResources().getDisplayMetrics().heightPixels);
        mPlayer = (VlcPlayer) findViewById(R.id.player);
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
}
