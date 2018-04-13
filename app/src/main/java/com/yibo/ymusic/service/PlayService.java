package com.yibo.ymusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/4/13.
 * 音乐播放服务
 * 功能:摇一摇播放下一曲
 */

public class PlayService extends Service implements MediaPlayer.OnCompletionListener{

    private static final String TAG = PlayService.class.getSimpleName();
    private SensorManager sensorManager;//传感器
    private MediaPlayer player;
    private OnMusicEventListener musicEventListener;
    private int playingPisition;//当前播放位置
    private PowerManager.WakeLock wakeLock=null;//获取设备电源锁,防止锁屏后服务停止
    private boolean isShaking;
    private Notification notification;//通知栏
    private RemoteViews remoteViews;//通知栏布局
    private NotificationManager notificationManager;

    //单线程池
    private ExecutorService progressUpdatedListener = Executors.newSingleThreadExecutor();

    public class PlayBinder extends Binder{
        public PlayService getService(){
            return PlayService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
        return new PlayBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(isShaking)return;
            if(Sensor.TYPE_ACCELEROMETER==event.sensor.getType()){
                float[] values = event.values;
                if(Math.abs(values[0])>8&&Math.abs(values[1])>8&&Math.abs(values[2])>8){
                    isShaking = true;
                    next();
                    //延迟200毫秒,防止抖动
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isShaking=false;
                        }
                    },200);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void next(){

    }
    /**
     * 音乐播放回调接口
     */
    public interface OnMusicEventListener{
        public void onPublish(int percent);
        public void onChange(int position);
    }
}
