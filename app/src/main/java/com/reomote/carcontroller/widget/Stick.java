package com.reomote.carcontroller.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.autofit.widget.ScreenParameter;
import com.autofit.widget.View;

/**
 * Created by dage on 2018/12/11.
 */

public class Stick extends com.autofit.widget.FrameLayout {
    public Stick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private View mPoint;
    private int mPointRadius = 25;
    private int mRadius;
    private float mLastX, mLastY;
    private ValueAnimator mResetAni = ValueAnimator.ofFloat(1.0f, 0f);

    private void init() {
        mPoint = new View(getContext());
        mPoint.setBackgroundColor(0x5eff0000);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mPointRadius * 2, mPointRadius * 2);
        params.gravity = Gravity.CENTER;
        addView(mPoint, params);
        mPointRadius = ScreenParameter.getFitSize(this, mPointRadius);
        mResetAni.setDuration(50);
        mResetAni.setInterpolator(new DecelerateInterpolator());
        mResetAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mPoint.setTranslationX(mLastX * value);
                mPoint.setTranslationY(mLastY * value);
            }
        });
    }

    private void calculate(float x, float y) {
        mResetAni.cancel();
        float dx = x - mRadius;
        float dy = y - mRadius;
        float dr = (float) Math.sqrt(dx * dx + dy * dy);
        if (dr > mRadius - mPointRadius) {
            dx = dx * (mRadius - mPointRadius) / dr;
            dy = dy * (mRadius - mPointRadius) / dr;
        }
        mPoint.setTranslationX(dx);
        mPoint.setTranslationY(dy);
        mLastX = dx;
        mLastY = dy;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                mRadius = getWidth() / 2;
                calculate(mLastX, mLastY);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastX = event.getX();
                mLastY = event.getY();
                calculate(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_UP:
                mResetAni.start();
                break;
        }
        return super.onTouchEvent(event);
    }
}
