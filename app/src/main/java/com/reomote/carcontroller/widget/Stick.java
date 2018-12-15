package com.reomote.carcontroller.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.autofit.widget.ScreenParameter;
import com.autofit.widget.View;
import com.reomote.carcontroller.R;

/**
 * Created by dage on 2018/12/11.
 */

public class Stick extends com.autofit.widget.FrameLayout {
    public Stick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public interface Callback {
        void onCallback(float degree, float ratio);
    }

    private View mPoint;
    private int mPointRadius = 33;
    private int mRadius;
    private float mLastX, mLastY;
    private ValueAnimator mResetAni = ValueAnimator.ofFloat(1.0f, 0f);
    private Drawable mLightDrawable;
    private Drawable mNaviDrawable;
    private Matrix mMatrix = new Matrix();
    private boolean mIsTouch = false;
    private Callback mCallback;

    private void init() {
        mLightDrawable = getResources().getDrawable(R.drawable.ic_light);
        mNaviDrawable = getResources().getDrawable(R.drawable.ic_fx);
        mPoint = new View(getContext());
        mPoint.setBackgroundResource(R.drawable.ic_f_light);
        mPoint.setVisibility(View.INVISIBLE);
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initSize();
        if (mIsTouch) {
            canvas.save();
            canvas.setMatrix(mMatrix);
            mLightDrawable.draw(canvas);
            canvas.restore();
        } else {
            mNaviDrawable.draw(canvas);
        }
    }

    private void initSize() {
        if (mRadius == 0 && getWidth() != 0) {
            int naviWidth = ScreenParameter.getFitWidth(this, 193);
            int width = getWidth();
            int height = getHeight();
            mNaviDrawable.setBounds((width - naviWidth) / 2, (height - naviWidth) / 2,
                    (width + naviWidth) / 2, (height + naviWidth) / 2);
            mLightDrawable.setBounds(0, 0, width, height);
            mRadius = width / 2 - ScreenParameter.getFitWidth(this, 5);
            invalidate();
            Log.d("big", "size:" + width + ",height:" + height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouch = true;
                mLastX = event.getX();
                mLastY = event.getY();
                calculate(mLastX, mLastY);
                mPoint.setVisibility(View.VISIBLE);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastX = event.getX();
                mLastY = event.getY();
                calculate(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_UP:
                mIsTouch = false;
                mResetAni.start();
                invalidate();
                mPoint.setVisibility(View.INVISIBLE);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void calculate(float x, float y) {
        mResetAni.cancel();
        float dx = x - mRadius;
        float dy = y - mRadius;
        float dr = (float) Math.sqrt(dx * dx + dy * dy);
        if (dr > mRadius - mPointRadius) {
            dx = dx * (mRadius - mPointRadius) / dr;
            dy = dy * (mRadius - mPointRadius) / dr;
            dr = mRadius - mPointRadius;
        }
        mPoint.setTranslationX(dx);
        mPoint.setTranslationY(dy);
        float degree = (float) Math.toDegrees(Math.acos(Math.abs(dx / dr)));
        if (dx < 0) {
            if (dy > 0) {
                degree = 180 + degree;
            } else {
                degree = 180 - degree;
            }
        } else if (dy > 0) {
            degree = 360 - degree;
        }
        mMatrix.setRotate(-(degree - 90), getWidth() / 2, getHeight() / 2);
        mLastX = dx;
        mLastY = dy;
        invalidate();
        if (mCallback != null) {
            float radio = dy < 0 ? -dr / (mRadius - mPointRadius) : dr / (mRadius - mPointRadius);
            mCallback.onCallback(-getRealDegree(degree), radio);
        }
    }

    private float getRealDegree(float degree) {
        if (degree < 90) {
            degree = degree - 90;
        } else if (degree < 180) {
            degree = degree - 90;
        } else if (degree < 270) {
            degree = 270 - degree;
        } else {
            degree = 270 - degree;
        }
        return degree;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
