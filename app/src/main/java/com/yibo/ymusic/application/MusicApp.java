package com.yibo.ymusic.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.yibo.ymusic.service.PlayService;

/**
 * Created by Administrator on 2018/4/13.
 */

public class MusicApp extends Application {

    public static Context aCOntext;
    public static int screenWith;
    public static int screenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        aCOntext = getApplicationContext();

        startService(new Intent(this, PlayService.class));

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWith = dm.widthPixels;
        screenHeight = dm.heightPixels;

    }
}
