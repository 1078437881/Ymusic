package com.yibo.ymusic.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2018/4/13.
 */

public class MusicApp extends Application {

    public static Context aCOntext;
    public static int aScreenWith;
    public static int aScreenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        aCOntext = getApplicationContext();

    }
}
