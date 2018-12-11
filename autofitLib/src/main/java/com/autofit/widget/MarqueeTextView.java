package com.autofit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 可改变速度的跑马灯TextView 使用说明：
 * 1、不需要设置android:ellipsize="marquee"和android:singleLine="true"，代码中已写死
 * 2、跟原生一样可通过setSelected方法控制是否滚动
 *
 * @author zhangjie
 */
public class MarqueeTextView extends TextView implements Runnable {
    private static final String TAG = "MarqueeTextView";

    public static final int SPEED_ONE = 1;// 最慢速度
    public static final int SPEED_TWO = 2;
    public static final int SPEED_THREE = 3;
    public static final int SPEED_FOUR = 4;
    public static final int SPEED_FIVE = 5;// 最快速度
    private int mScrollPos = 0;
    private int mSpeed = 3;
    private int mSpeedMode = SPEED_THREE;
    private int mTextWidth = 0;
    private boolean isMeasured = false;

    /**
     * 设置速度模式，共有五个档位
     *
     * @param speedMode
     */
    public void setSpeedMode(int speedMode) {
        mSpeedMode = speedMode;
        getSpeed();
    }

    private void getSpeed() {

        switch (mSpeedMode) {
        case SPEED_ONE:
            mSpeed = 1;
            break;
        case SPEED_TWO:
            mSpeed = 2;
            break;
        case SPEED_THREE:
            mSpeed = 3;
            break;
        case SPEED_FOUR:
            mSpeed = 4;
            break;
        case SPEED_FIVE:
            mSpeed = 5;
            break;
        default:
            mSpeed = 3;
            break;
        }
        mSpeed = (int) (mSpeed * getTextSize() / 10);
        if (mSpeed <= 0) {
            mSpeed = 1;
        }
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getSpeed();
        this.setEllipsize(null);
        setSingleLine();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isSelected() || this.hasFocus()) {
            startScroll();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopScroll();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isMeasured) {
            getTextWidth();
            isMeasured = true;
        }
    }

    private void getTextWidth() {
        Paint paint = this.getPaint();
        String text = this.getText().toString();
        if (text == null) {
            mTextWidth = 0;
        } else {
            mTextWidth = (int) paint.measureText(text);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        isMeasured = false;
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            if (this.isSelected() || this.hasFocus()) {
                stopScroll();
                startScroll();
            }
        } else {
            stopScroll();
        }
    }

    @Override
    public void run() {
        if (mTextWidth > this.getWidth()) {
            mScrollPos += mSpeed;
            scrollTo(mScrollPos, 0);
            if (mScrollPos >= mTextWidth) {
                mScrollPos = -this.getWidth();
            }
            postDelayed(this, 100);
        }
    }

    private void startScroll() {
        // LogUtil.d(TAG, "startScroll");
        if (this.getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
        this.removeCallbacks(this);
        postDelayed(this, 600);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        isMeasured = false;
        super.setText(text, type);
        if (getVisibility() == View.VISIBLE) {
            if (this.isSelected() || this.hasFocus()) {
                stopScroll();
                startScroll();
            }
        }
    }

    @Override
    public void setTextSize(float size) {
        isMeasured = false;
        super.setTextSize(size);
    }

    @Override
    public void setTextSize(int unit, float size) {
        isMeasured = false;
        super.setTextSize(unit, size);
    }

    private void stopScroll() {
        // LogUtil.d(TAG, "stopScroll");
        this.removeCallbacks(this);
        mScrollPos = 0;
        scrollTo(mScrollPos, 0);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            startScroll();
        } else {
            stopScroll();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            startScroll();
        } else {
            stopScroll();
        }
    }
}