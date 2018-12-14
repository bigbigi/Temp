package com.reomote.carcontroller.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;

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

public class VideoView extends FrameLayout implements IVLCVout.Callback {
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
            mPlayer.getVLCVout().setWindowSize(-1,-1);
            mPlayer.getVLCVout().addCallback(this);
            mPlayer.getVLCVout().setVideoSurface(mSurfaceView.getHolder().getSurface(), mSurfaceView.getHolder());
            mPlayer.getVLCVout().attachViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVideoPath(String path) {
        if (mPlayer == null) {
            init();
        }
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

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
        Log.d(TAG, "onSurfacesCreated：");
        mPlayer.getVLCVout().setWindowSize(2048,1440);
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
        Log.d(TAG, "onSurfacesDestroyed：");
        stop();
    }
}
