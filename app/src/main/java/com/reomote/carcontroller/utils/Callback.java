package com.reomote.carcontroller.utils;

/**
 * Created by big on 2018/11/15.
 */

public interface Callback<T> {
    void onCallBack(boolean success, T... t);
}
