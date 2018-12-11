package com.autofit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;


import net.sf.chineseutils.ChineseUtils;

public class TextView extends android.widget.TextView implements IAutoFit {

    private boolean mEnableAutoFit = true;

    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
        this.setTextSize(getTextSize());
        mEnableAutoFit = ScreenParameter.getEnableAutoFit(context, attrs);
        this.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        if (Build.VERSION.SDK_INT > 15) {
            setLineSpacing(getLineSpacingExtra(), getLineSpacingMultiplier());
        }
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
        setTextSize(0, ScreenParameter.getFitHeight(this, (int) size));
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(ScreenParameter.getFitSize(this, pad));
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(ScreenParameter.getFitHeight(getContext(), (int) add), mult);
    }

    private int padingCount = 0;

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (padingCount == 0) {
            super.setPadding(ScreenParameter.getFitWidth(this, left), ScreenParameter.getFitHeight(this, top),
                    ScreenParameter.getFitWidth(this, right), ScreenParameter.getFitHeight(this, bottom));
            padingCount++;
        } else {
            super.setPadding(left, top, right, bottom);
        }
    }

    private int layoutCount = 0;

    @Override
    public void setLayoutParams(LayoutParams params) {
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
    public void setMaxWidth(int maxpixels) {
        super.setMaxWidth(maxpixels == -1 || maxpixels == Integer.MAX_VALUE ? maxpixels : ScreenParameter.getFitWidth(
                this.getContext(), maxpixels, true));
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
        if (!closeFt && ScreenParameter.isFT && text != null && text.length() > 0 && !(text instanceof SpannableString)) {
            super.setText(ChineseUtils.simpToTrad(text.toString()), type);
        } else {
            if (!TextUtils.isEmpty(text) && text.toString().contains("-")) {
                text = text.toString().replaceAll("-", "- ");
            }
            super.setText(text, type);
        }
    }

    private boolean closeFt = false;

    /**
     * 不使用繁体显示
     */
    public void closeFt() {
        closeFt = true;
    }
}
