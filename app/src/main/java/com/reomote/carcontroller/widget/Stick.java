package com.reomote.carcontroller.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import com.autofit.widget.FrameLayout;
import com.autofit.widget.ScreenParameter;
import com.autofit.widget.View;

/**
 * Created by dage on 2018/12/11.
 */

public class Stick extends com.autofit.widget.FrameLayout{
    public Stick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private View mPoint;

    private void init(){
        mPoint=new View(getContext());
        mPoint.setBackgroundColor(0x5eff0000);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(50,50);
        params.gravity=Gravity.CENTER;
        addView(mPoint,params);
        mPoint.setTranslationX(ScreenParameter.getFitWidth(getContext(),50));
    }
}
