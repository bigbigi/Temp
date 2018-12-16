package com.reomote.carcontroller.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by big on 2018/11/26.
 */

public class Utils {
    public static final boolean hasInstall(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> list = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        if (list != null) {
            for (PackageInfo info : list) {
                if (info.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean startPackage(Context context, String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int parseInt(String str) {
        return parseInt(str, -1);
    }

    public static float parseFloat(String str) {
        return parseFloat(str, -1);
    }

    public static float parseFloat(String str, float def) {
        float i = def;
        if (!TextUtils.isEmpty(str)) {
            try {
                i = Float.parseFloat(str.trim());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static int parseInt(String str, int def) {
        int i = def;
        if (!TextUtils.isEmpty(str)) {
            try {
                i = Integer.parseInt(str.trim());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return i;
    }
}
