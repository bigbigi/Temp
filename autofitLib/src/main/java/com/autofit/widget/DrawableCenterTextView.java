package com.autofit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

public class DrawableCenterTextView extends TextView {

    private boolean drawableInCenter = true;

    public DrawableCenterTextView(Context context) {
        super(context);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (drawableInCenter) {
            Drawable[] drawables = getCompoundDrawables();
            Drawable drawableTop = drawables[1];
            setPadding(0, 0, 0, 0);
            if (drawableTop != null) {
                getPaint().setTextSize(getTextSize());
                FontMetrics fm = getPaint().getFontMetrics();
                float textHeight = (float) (Math.ceil(fm.descent - fm.top) + ScreenParameter.getFitHeight(this, 2));
                int drawablePadding = getCompoundDrawablePadding();
                int drawableHeight = drawableTop.getIntrinsicHeight();
                float bodyHeight = textHeight + drawableHeight + drawablePadding;
                float excursion = (getHeight() - bodyHeight) / 2.0f;
                canvas.translate(0, excursion);
                setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                setGravity(Gravity.CENTER);
            }
        }
        super.onDraw(canvas);
    }

    public void setDrawableInCenter(boolean drawableInCenter) {
        this.drawableInCenter = drawableInCenter;
    }

    @Override
    public void setTypeface(Typeface tf) {
        Typeface newTf = null;
        if (ScreenParameter.isNeedSetFont) {
            newTf = ScreenParameter.getTypeFace(getContext().getApplicationContext());
        }
        if (!ScreenParameter.isFT && newTf != null) {
            super.setTypeface(newTf);
        } else {
            super.setTypeface(tf);
        }
    }
}
