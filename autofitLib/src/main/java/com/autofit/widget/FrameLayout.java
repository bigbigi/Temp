package com.autofit.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class FrameLayout extends android.widget.FrameLayout implements IAutoFit {

    private boolean mEnableAutoFit = true;

    public FrameLayout(Context context) {
        super(context);
    }

    public FrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public FrameLayout(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        initAutoView(context, attrs);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
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
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (layoutCount == 0) {
            super.setLayoutParams(ScreenParameter.getRealLayoutParams(this, params));
            layoutCount++;
        } else {
            super.setLayoutParams(params);
        }
    }

    public void setAutoLayoutParams(ViewGroup.LayoutParams params) {
        layoutCount = 1;
        super.setLayoutParams(ScreenParameter.getRealLayoutParams(this, params));
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
    public void setScaleX(float scaleX) {
        boolean needRebuild = scaleX != getScaleX();
        super.setScaleX(scaleX);
        if (needRebuild) {
            invalidateParent();
        }
    }

    @Override
    public void setScaleY(float scaleY) {
        boolean needRebuild = scaleY != getScaleY();
        super.setScaleY(scaleY);
        if (needRebuild) {
            invalidateParent();
        }
    }

    @Override
    public void setTranslationX(float translationX) {
        boolean needRebuild = translationX != getTranslationX();
        super.setTranslationX(translationX);
        if (needRebuild) {
            invalidateParent();
        }
    }

    @Override
    public void setTranslationY(float translationY) {
        boolean needRebuild = translationY != getTranslationY();
        super.setTranslationY(translationY);
        if (needRebuild) {
            invalidateParent();
        }
    }

    private void invalidateParent() {
        if (Build.VERSION.SDK_INT > 17) return;
        android.view.View temp = this;
        while (temp.getParent() != null && temp.getParent() instanceof android.view.View) {
            temp = (android.view.View) temp.getParent();
            if (temp instanceof RecyclerView) {
                temp.invalidate();
                break;
            }
        }
    }
}
