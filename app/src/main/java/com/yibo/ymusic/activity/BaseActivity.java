package com.yibo.ymusic.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yibo.ymusic.service.PlayService;
import com.yibo.ymusic.utils.MyLog;

/**
 * Created by Administrator on 2018/4/28.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected PlayService playService;
    private final String TAG = BaseActivity.class.getSimpleName();

    private ServiceConnection playServiceCOnnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            playService = ((PlayService.PlayBinder) service).getService();
            playService.setOnMusicEventListener(musicEventListener);
            onChange(playService.getPlayingPisition());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLog.d(TAG,"play-->onServiceDisconnected");
            playService = null;
        }
    };

    private PlayService.OnMusicEventListener musicEventListener = new PlayService.OnMusicEventListener() {
        @Override
        public void onPublish(int progress) {
            BaseActivity.this.onPublicsh(progress);

        }

        @Override
        public void onChange(int position) {
            BaseActivity.this.onChange(position);
        }
    };

    /**
     * Fragment的view加载完成后回调
     *
     * 注意：
     * allowBindService()使用绑定的方式启动歌曲播放的服务
     * allowUnbindService()方法解除绑定
     *
     * 在SplashActivity.java中使用startService()方法启动过该音乐播放服务了
     * 那么大家需要注意的事，该服务不会因为调用allowUnbindService()方法解除绑定
     * 而停止。
     */
    public void allowBingsService(){
        getApplicationContext().bindService(new Intent(this,PlayService.class),
                playServiceCOnnection,
                Context.BIND_AUTO_CREATE);
    }

    /**
     * fragment的view消失后回调
     */
    public void allowUnbindService(){
        getApplicationContext().unbindService(playServiceCOnnection);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定服务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 更新进度
     * 抽象方法有子类实现
     * 实现service与主界面通信
     * @param progress 进度
     */
    public abstract void onPublicsh(int progress);

    /**
     * 切换歌曲
     * 抽象方法有子类实现
     * 实现service与主界面通信
     * @param position 歌曲在list中的位置
     */
    public abstract void onChange(int position);
}
