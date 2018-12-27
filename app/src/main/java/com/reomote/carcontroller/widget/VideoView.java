package com.reomote.carcontroller.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import com.autofit.widget.FrameLayout;
import com.autofit.widget.SurfaceView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by big on 2018/12/14.
 */

public class VideoView extends FrameLayout implements IVLCVout.Callback, MediaPlayer.EventListener {
    private final static String TAG = "VideoView";
    private MediaPlayer mPlayer = null;
    private LibVLC mLibVLC;
    private SurfaceView mSurfaceView;

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceView = new SurfaceView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mSurfaceView.setLayoutParams(layoutParams);
        this.addView(mSurfaceView);
        mSurfaceView.setKeepScreenOn(true);
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        ArrayList<String> options = new ArrayList<>();
        mLibVLC = new LibVLC(getContext(), options);
        try {
            mPlayer = new MediaPlayer(mLibVLC);
            mPlayer.getVLCVout().addCallback(this);
            mPlayer.getVLCVout().setVideoSurface(mSurfaceView.getHolder().getSurface(), mSurfaceView.getHolder());
            mPlayer.getVLCVout().attachViews();
            mPlayer.setEventListener(this);
            mPlayer.setScale(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVideoPath(String path) {
        if (mPlayer == null) {
            init();
        }
        Log.d(TAG, "path:" + path);
        mPath = path;
        mPlayer.setMedia(new Media(mLibVLC, Uri.parse(path)));
        play();
    }

    public void play() {
        Log.d(TAG, "play：" + mPlayer.getVLCVout().areViewsAttached());
        if (mPlayer != null) {
            mPlayer.play();
        }
    }

    public void pause() {
        Log.d(TAG, "pause");
        mPlayer.pause();
    }

    private void stop() {
        Log.d(TAG, "stop");
        mPlayer.stop();
        mPlayer.release();
        mPlayer.getVLCVout().removeCallback(this);
        mPlayer = null;
    }

    public void release() {
        mPlayer.setEventListener(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
        Log.d(TAG, "onSurfacesCreated：");
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int dw = display.getWidth();
        int dh = display.getHeight();
        mPlayer.getVLCVout().setWindowSize(dw, dh);
        mPlayer.setAspectRatio(dw + ":" + dh);
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
        Log.d(TAG, "onSurfacesDestroyed：");
        stop();
    }

    private String mPath;
    private boolean mIsPlaying = false;
    private static final int MSG_RETRY = 1;
    private static final int DURATION = 300;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RETRY:
                    if (!mIsPlaying) {
                        setVideoPath(mPath);
                        sendEmptyMessageDelayed(MSG_RETRY, DURATION);
                    }
                    break;
            }
        }
    };

    @Override
    public void onEvent(MediaPlayer.Event event) {
        switch (event.type) {
            case MediaPlayer.Event.Opening:
            case MediaPlayer.Event.Buffering:
            case MediaPlayer.Event.Playing://260
                Log.d(TAG, "onEvent:Playing");
                mIsPlaying = true;
                mHandler.removeMessages(MSG_RETRY);
                break;
            case MediaPlayer.Event.Stopped://262
                Log.d(TAG, "onEvent:stop");
                mIsPlaying = false;
                mHandler.sendEmptyMessageDelayed(MSG_RETRY, DURATION);
                break;
        }
    }
}
