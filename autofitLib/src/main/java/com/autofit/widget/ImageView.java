package com.autofit.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;


public class ImageView extends android.widget.ImageView implements IAutoFit {

    private boolean mEnableAutoFit = true;
    //    private boolean isShowAnimation = true;
    boolean isAutoFitXY = true;

    public ImageView(Context context) {
        super(context);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
        this.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        mEnableAutoFit = ScreenParameter.getEnableAutoFit(context, attrs);
        isAutoFitXY = ScreenParameter.getAutoFitXY(context, attrs);
        if (isAutoFitXY) {
            setScaleType(ScaleType.FIT_XY);
        }
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

//    @Override
//    public void setImageResource(int resId) {
//        if (isShowAnimation && (getAnimation() == null || !getAnimation().hasStarted())) {
//            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_action);
//            startAnimation(anim);
//        }
//        super.setImageResource(resId);
//    }

    @Override
    public boolean getEnabledAutoFit() {
        return mEnableAutoFit;
    }

    @Override
    public void setEnabledAutoFit(boolean autofit) {
        this.mEnableAutoFit = autofit;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (isAutoFitXY) {
            super.setScaleType(ScaleType.FIT_XY);
        } else {
            super.setScaleType(scaleType);
        }
    }

    public void setAutoFitXY(boolean isAutoFitXY) {
        this.isAutoFitXY = isAutoFitXY;
    }

    private int padingCount = 0;

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (padingCount == 0 && mEnableAutoFit) {
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
        if (layoutCount == 0 && mEnableAutoFit) {
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

//    public void setIsShowAnimation(boolean isShowAnimation) {
//        this.isShowAnimation = isShowAnimation;
//    }

//    @Override
//    public void setImageBitmap(Bitmap bm) {
//        if (isShowAnimation && (getAnimation() == null || !getAnimation().hasStarted())) {
//            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_action);
//            startAnimation(anim);
//        }
//        super.setImageBitmap(bm);
//    }
//
//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        if (isShowAnimation && (getAnimation() == null || !getAnimation().hasStarted())) {
//            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_action);
//            startAnimation(anim);
//        }
//        super.setImageDrawable(drawable);
//    }
}
