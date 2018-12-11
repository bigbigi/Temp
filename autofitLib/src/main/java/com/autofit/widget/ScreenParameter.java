package com.autofit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;


import com.amway.wifianalyze.lib.R;

import java.lang.reflect.Method;

/**
 * @author 武江南
 * @ClassName: ScreenParameter
 * @Description: TODO(获取屏幕的一些参数)
 * @date 2015-4-3 下午7:19:46
 */
public final class ScreenParameter {

    private static float sDensity;
    private static int sWidth, sHeight;
    private static int sDensityDpi;
    private static float sDefaultWidth = 1334.0f;
    private static float sDefaultHeight = 750.0f;
    private static float sRatioX = 0;
    private static float sRatioY = 0;
    private static float sRadtio;

    private static boolean isVertical = false;

    public static boolean isFT = false;
    public static boolean isNeedSetFont = true;//是否需要设置字体


    /**
     * @throws
     * @Title: setVerticalMode
     * @Description: TODO(设置是否为手机版适配模式 ，true是竖屏模式，false为横屏模式 ，默认为横屏模式。)
     */
    public static void setVerticalMode(boolean isVertical) {
        ScreenParameter.isVertical = isVertical;
    }

    public static int getScreenHeight(Context context) {
        if (sHeight == 0) {
            synchronized (ScreenParameter.class) {
                if (sHeight == 0) {
                    return getDisplaySize(context, true);
                }
            }
        }
        return sHeight;
    }

    public static int getScreenWidth(Context context) {
        if (sWidth == 0) {
            synchronized (ScreenParameter.class) {
                if (sWidth == 0) {
                    return getDisplaySize(context, false);
                }
            }
        }
        return sWidth;
    }

    public static float getRadtio(Context ctx) {
        if (sRadtio == 0) {
            synchronized (ScreenParameter.class) {
                if (sRadtio == 0) {
                    sRadtio = Math.min(getRatioX(ctx), getRatioY(ctx));
                }
            }
        }
        return sRadtio;
    }

    public static float getRatioX(Context context) {
        if (sRatioX == 0) {
            synchronized (ScreenParameter.class) {
                if (sRatioX == 0) {
                    if (isVertical) {
                        sRatioX = Math.min(getScreenWidth(context), getScreenHeight(context)) / sDefaultHeight;
                    } else {
                        sRatioX = Math.max(getScreenWidth(context), getScreenHeight(context)) / sDefaultWidth;
                    }
                }
            }
        }
        return sRatioX;
    }

    public static float getRatioY(Context context) {
        if (sRatioY == 0) {
            synchronized (ScreenParameter.class) {
                if (sRatioY == 0) {
                    if (isVertical) {
                        sRatioY = Math.max(getScreenWidth(context), getScreenHeight(context)) / sDefaultWidth;
                    } else {
                        sRatioY = Math.min(getScreenWidth(context), getScreenHeight(context)) / sDefaultHeight;
                    }
                }
            }
        }
        return sRatioY;
    }

    /**
     * 根据设计图的标注标准来配置基础缩放的宽高
     *
     * @param width  宽
     * @param height 高
     */
    public static void setMeasureWidthAndHeight(float width, float height) {
        sDefaultWidth = width;
        sDefaultHeight = height;
    }


    /**
     * @param @param  width
     * @param @return 设定文件
     * @return float 返回类型
     * @throws
     * @Description: TODO(根据设计图的标准不同配置标准宽)
     */
    public static float setMeasureWidth(float width) {
        sDefaultWidth = width;
        return sDefaultWidth;
    }

    /**
     * @param @param  height
     * @param @return 设定文件
     * @return float 返回类型
     * @throws
     * @Description: TODO(根据设计图的标准不同配置标准高)
     */
    public static float setMeasureHeight(float height) {
        sDefaultHeight = height;
        return sDefaultHeight;
    }

    public static float getDenSity(Context context) {
        if (sDensity == 0) {
            synchronized (ScreenParameter.class) {
                if (sDensity == 0) {
                    DisplayMetrics metric = new DisplayMetrics();
                    ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay().getMetrics(metric);
                    sDensity = metric.density;
                }
            }
        }
        return sDensity;
    }

    public static int getDensityDpi(Context context) {
        if (sDensityDpi == 0) {
            synchronized (ScreenParameter.class) {
                if (sDensityDpi == 0) {
                    DisplayMetrics metric = new DisplayMetrics();
                    ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay().getMetrics(metric);
                    sDensityDpi = metric.densityDpi;
                }
            }
        }
        return sDensityDpi;
    }

    public static int getFitSize(View v, int size) {
        boolean isAutoFit = true;
        if (v instanceof IAutoFit) {
            IAutoFit iAutoFit = (IAutoFit) v;
            isAutoFit = iAutoFit.getEnabledAutoFit();
        }
        return getFitSize(v.getContext(), size, isAutoFit);
    }

    public static float getFitSize(View v, float size) {
        boolean isAutoFit = true;
        if (v instanceof IAutoFit) {
            IAutoFit iAutoFit = (IAutoFit) v;
            isAutoFit = iAutoFit.getEnabledAutoFit();
        }
        return getFitSize(v.getContext(), size, isAutoFit);
    }

    public static int getFitSize(Context context, int size) {
        return getFitSize(context, size, true);
    }

    public static int getFitSize(Context context, int size, boolean autoFit) {
        if (!autoFit) {
            return size;
        }
        float radio = getRadtio(context);
        if (Math.abs(getRatioX(context) - getRatioY(context)) < 0.15) {
            radio = getRatioX(context);
        }
        int result = (int) (radio * size);
        return (result != 0 || size == 0) ? result : (size > 0 ? 1 : -1);
    }

    public static int getFitWidth(Context context, int size) {
        return getFitSize(context, size, true);
    }

    public static int getFitWidth(View v, int size) {
        return getFitSize(v, size);
    }

    public static int getFitWidth(Context context, int size, boolean autoFit) {
        return getFitSize(context, size, autoFit);
    }

    public static float getFitSize(Context context, float size, boolean autoFit) {
        if (!autoFit) {
            return size;
        }

        int result = (int) (getRatioX(context) * size);
        return (result != 0 || size == 0) ? result : (size > 0 ? 1 : -1);
    }

    public static int getFitHeight(Context context, int size) {
        return getFitHeight(context, size, true);
    }

    public static int getFitHeight(View v, int size) {
        boolean isAutoFit = true;
        if (v instanceof IAutoFit) {
            IAutoFit iAutoFit = (IAutoFit) v;
            isAutoFit = iAutoFit.getEnabledAutoFit();
        }
        return getFitHeight(v.getContext(), size, isAutoFit);
    }

    public static int getFitHeight(Context context, int size, boolean autoFit) {
        if (!autoFit) {
            return size;
        }
        float radio = getRadtio(context);
        if (Math.abs(getRatioX(context) - getRatioY(context)) < 0.15) {
            radio = getRatioY(context);
        }
        int result = (int) (radio * size);
        return (result != 0 || size == 0) ? result : (size > 0 ? 1 : -1);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * getDenSity(context) + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDenSity(context) + 0.5f);
    }

    public static android.view.ViewGroup.LayoutParams getRealLayoutParams(View v,
                                                                          android.view.ViewGroup.LayoutParams params) {
        boolean isAutoFit = true;
        if (v instanceof IAutoFit) {
            IAutoFit iAutoFit = (IAutoFit) v;
            isAutoFit = iAutoFit.getEnabledAutoFit();
        }
        if (!isAutoFit) {
            return params;
        }
        if (params instanceof MarginLayoutParams) {
            ((MarginLayoutParams) params).leftMargin = ScreenParameter.getFitWidth(v,
                    ((MarginLayoutParams) params).leftMargin);
            ((MarginLayoutParams) params).rightMargin = ScreenParameter.getFitWidth(v,
                    ((MarginLayoutParams) params).rightMargin);
            ((MarginLayoutParams) params).topMargin = ScreenParameter.getFitHeight(v,
                    ((MarginLayoutParams) params).topMargin);
            ((MarginLayoutParams) params).bottomMargin = ScreenParameter.getFitHeight(v,
                    ((MarginLayoutParams) params).bottomMargin);
        }
        boolean isSquare = false;// 是否是一个正方形
        if (params.width != LayoutParams.WRAP_CONTENT && params.width != LayoutParams.MATCH_PARENT) {
            isSquare = params.width == params.height;
            params.width = ScreenParameter.getFitWidth(v, params.width);
        }
        if (params.height != LayoutParams.WRAP_CONTENT && params.height != LayoutParams.MATCH_PARENT) {
            if (isSquare) {// 如果是一个正方形，长和宽的缩放系数一致
                params.height = ScreenParameter.getFitWidth(v, params.height);
            } else {
                params.height = ScreenParameter.getFitHeight(v, params.height);
            }
        }
        return params;
    }

    public static boolean getEnableAutoFit(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VST_AUTO_FIT);
        boolean ret = array.getBoolean(0, true);
        array.recycle();
        return ret;
    }

    public static boolean getAutoFitXY(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageView);
        boolean isAutoFitXY = array.getBoolean(R.styleable.ImageView_autoFitXY, true);
        array.recycle();
        return isAutoFitXY;
    }


    public static boolean getShowAnmimation(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VST_IMG_ANIMATION);
        boolean ret = array.getBoolean(0, true);
        array.recycle();
        return ret;
    }

    public static int getSrcId(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VST_IMG_SRC);
        int id = array.getResourceId(0, -1);
        array.recycle();
        return id;
    }

    public static int getBackgroundId(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VST_IMG_BACKGROUND);
        int id = array.getResourceId(0, -1);
        array.recycle();
        return id;
    }

    public static int[] getTextViewDrawableIds(Context context, AttributeSet attrs) {
        int[] ids = new int[4];
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VST_TEXTVIEW_DRAWABLE);
        for (int i = 0; i < 4; i++) {
            ids[i] = array.getResourceId(i, -1);
        }
        array.recycle();
        return ids;
    }

    private static Typeface sTypeFace;

    public static Typeface getTypeFace(Context ctx) {
        return null;
       /* if (sTypeFace == null) {
            synchronized (ScreenParameter.class) {
                if (sTypeFace == null) {
                    try {
                        sTypeFace = Typeface.createFromAsset(ctx.getAssets(), "fonts/flfbls.ttf");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sTypeFace;*/
    }

    public static double getScreenDimension(Context ctx) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }

    public static int getDisplaySize(Context context, boolean getHeight) {
        if (sWidth * sHeight == 0) {
            // int sdkVer = Build.VERSION.SDK_INT;
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            display.getMetrics(dm);
            sWidth = dm.widthPixels;
            sHeight = dm.heightPixels;
            // try {
            // if (!checkDeviceHasNavigationBar(context)) {
            // if (sdkVer == 13) {
            // Method realHeight =
            // display.getClass().getMethod("getRealHeight");
            // sHeight = (Integer) realHeight.invoke(display);
            // Method realWidth = display.getClass().getMethod("getRealWidth");
            // sWidth = (Integer) realWidth.invoke(display);
            // } else if (sdkVer > 13 && sdkVer < 17) {
            // Method rawHeight = display.getClass().getMethod("getRawHeight");
            // sHeight = (Integer) rawHeight.invoke(display);
            // Method rawWidth = display.getClass().getMethod("getRawWidth");
            // sWidth = (Integer) rawWidth.invoke(display);
            // } else if (sdkVer >= 17) { // 4.2
            // Method mt = display.getClass().getMethod("getRealSize",
            // Point.class);
            // Point point = new Point();
            // mt.invoke(display, point);
            // sWidth = point.x;
            // sHeight = point.y;
            // }
            // }
            // } catch (Throwable e) {
            // e.printStackTrace();
            // }
            // if (sWidth * sHeight == 0) {// 防止sdk反射出现异常，则用系统方法获取屏幕宽高
            // sWidth = dm.widthPixels;
            // sHeight = dm.heightPixels;
            // }
        }
        if (getHeight) {
            return sHeight;
        } else {
            return sWidth;
        }
    }

    public static int getDisplayHeight(Context context) {
        if (sWidth * sHeight == 0) {
            int sdkVer = Build.VERSION.SDK_INT;
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            display.getMetrics(dm);
            // sWidth = dm.widthPixels;
            sHeight = dm.heightPixels;
            try {
                // if (!checkDeviceHasNavigationBar(context)) {
                if (sdkVer == 13) {
                    Method realHeight = display.getClass().getMethod("getRealHeight");
                    sHeight = (Integer) realHeight.invoke(display);
                    // Method realWidth =
                    // display.getClass().getMethod("getRealWidth");
                    // sWidth = (Integer) realWidth.invoke(display);
                } else if (sdkVer > 13 && sdkVer < 17) {
                    Method rawHeight = display.getClass().getMethod("getRawHeight");
                    sHeight = (Integer) rawHeight.invoke(display);
                    // Method rawWidth =
                    // display.getClass().getMethod("getRawWidth");
                    // sWidth = (Integer) rawWidth.invoke(display);
                } else if (sdkVer >= 17) { // 4.2
                    Method mt = display.getClass().getMethod("getRealSize", Point.class);
                    Point point = new Point();
                    mt.invoke(display, point);
                    // sWidth = point.x;
                    sHeight = point.y;
                }
                // }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (sWidth * sHeight == 0) {// 防止sdk反射出现异常，则用系统方法获取屏幕宽高
                // sWidth = dm.widthPixels;
                sHeight = dm.heightPixels;
            }
        }
        return sHeight;
    }
}
