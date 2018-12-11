package com.reomote.carcontroller.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

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
    private int mPointRadius=25;
    private int mRadius;

    private void init(){
        mPoint=new View(getContext());
        mPoint.setBackgroundColor(0x5eff0000);
        mPointRadius= ScreenParameter.getFitSize(this,mPointRadius);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(mPointRadius*2,mPointRadius*2);
        params.gravity=Gravity.CENTER;
        addView(mPoint,params);
    }
private void calculate(float x,float y){
    float dx=x-mRadius;
    float dy=y-mRadius;
    float dr= (float) Math.sqrt(dx*dx+dy*dy);
    if(dr<=mRadius-mPointRadius){
        mPoint.setTranslationX(dx);
        mPoint.setTranslationY(dy);
    }else{
        mPoint.setTranslationX(dx*(mRadius-mPointRadius)/dr);
        mPoint.setTranslationY(dy*(mRadius-mPointRadius)/dr);
    }
    Log.d("big","dr:"+dr+","+(mRadius-mPointRadius));

}
    private float lastX,lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
           case MotionEvent.ACTION_DOWN:
                lastX=event.getX();
                lastY=event.getY();
                mRadius=getWidth()/2;
                calculate(lastX,lastY);
               return true;
            case MotionEvent.ACTION_MOVE:
                lastX=event.getX();
                lastY=event.getY();
                calculate(lastX,lastY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }
}
