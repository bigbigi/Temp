package com.autofit.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import net.sf.chineseutils.ChineseUtils;

public class RadioButton extends android.widget.RadioButton implements IAutoFit {

    private boolean mEnableAutoFit = true;

    public RadioButton(Context context) {
        super(context);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
        this.setTextSize(getTextSize());
        mEnableAutoFit = ScreenParameter.getEnableAutoFit(context, attrs);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }


    @Override
    public boolean getEnabledAutoFit() {
        return mEnableAutoFit;
    }

    @Override
    public void setEnabledAutoFit(boolean autofit) {
        this.mEnableAutoFit = autofit;
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenParameter.getFitSize(this, (int) size));
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenParameter.getFitSize(this, (int) size));
    }

    private int padingCount = 0;

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (padingCount == 0) {
            super.setPadding(ScreenParameter.getFitWidth(this, left),
                    ScreenParameter.getFitHeight(this, top), ScreenParameter.getFitWidth(this, right),
                    ScreenParameter.getFitHeight(this, bottom));
            padingCount++;
        } else {
            super.setPadding(left, top, right, bottom);
        }
    }

    private int layoutCount = 0;

    @Override
    public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
        if (layoutCount == 0) {
            super.setLayoutParams(ScreenParameter.getRealLayoutParams(this, params));
            layoutCount++;
        } else {
            super.setLayoutParams(params);
        }
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(ScreenParameter.getFitHeight(this, minHeight));
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(ScreenParameter.getFitWidth(this, minWidth));
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


    @Override
    public void setText(CharSequence text, BufferType type) {
        if (ScreenParameter.isFT && text != null && text.length() > 0) {
            super.setText(ChineseUtils.simpToTrad(text.toString()), type);
        } else {
            super.setText(text, type);
        }
    }
}
