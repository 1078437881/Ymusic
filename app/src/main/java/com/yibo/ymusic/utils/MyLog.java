package com.yibo.ymusic.utils;

import android.util.Log;

/**
 * Created by Administrator on 2018/4/18.
 */

public class MyLog {
    private static boolean showLog = true;

    public static void setShowLog(boolean showLog) {
        MyLog.showLog = showLog;
    }

    public static void d(String TAG, String msg){
        if(showLog){
            Log.d(TAG,msg);
        }
    }
    public static void e(String TAG, String msg){
        if(showLog){
            Log.e(TAG,msg);
        }
    }
}
