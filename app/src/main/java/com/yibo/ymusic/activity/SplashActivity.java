package com.yibo.ymusic.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.yibo.ymusic.R;
import com.yibo.ymusic.utils.Constants;

public class SplashActivity extends Activity {

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private static final String[] permissions = new String []{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean isAllGranted = checkPermissionAllGranted(permissions);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            Constants.ALL_GRANTED = true;
//            startService(new Intent(getApplicationContext(), PlayService.class));
        }else{
            // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_REQUEST_CODE);
        }

        //2秒后跳转到主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainMusicActivity.class));
            }
        },2000);
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                Constants.ALL_GRANTED = true;
            }
        }
    }
}
