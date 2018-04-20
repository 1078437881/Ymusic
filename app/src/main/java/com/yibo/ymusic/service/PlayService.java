package com.yibo.ymusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.yibo.ymusic.R;
import com.yibo.ymusic.activity.PlayerActivity;
import com.yibo.ymusic.utils.Constants;
import com.yibo.ymusic.utils.MusicUtils;
import com.yibo.ymusic.utils.MyLog;
import com.yibo.ymusic.utils.SpUtils;

import java.net.URI;
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



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();//获取设备电源锁
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        MusicUtils.intitMusicList();
        playingPisition =(Integer) SpUtils.get(this, Constants.PLAY_POS,0);

        if(MusicUtils.musicArrayList.size()<=0){
            Toast.makeText(getApplicationContext(), "当前手机没有MP3文件", Toast.LENGTH_SHORT).show();
        }else{
            if (getPlayingPisition()<0){
                playingPisition=0;
            }
            Uri uri = Uri.parse(MusicUtils.musicArrayList.get(getPlayingPisition()).getUri());
            player = MediaPlayer.create(PlayService.this,uri);
            player.setOnCompletionListener(this);
        }
        //开始更新进度的线程
        progressUpdatedListener.execute(publishProgressRunnable);

        //该方法虽然被抛弃过时,但是通用
        PendingIntent pendingIntent = PendingIntent.getActivity(PlayService.this,0,
                new Intent(PlayService.this, PlayerActivity.class),0);
        remoteViews = new RemoteViews(getPackageName(), R.layout.play_notifination);
        notification = new Notification(R.drawable.ic_launcher_background,"歌曲正在播放",System.currentTimeMillis());
        notification.contentIntent = pendingIntent;
        notification.contentView = remoteViews;
        //标记位,设置通知栏一直存在
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        Intent intent = new Intent(PlayService.class.getSimpleName());
        intent.putExtra("BUTTON_NF",1);
    }

    /**
     * 播放完毕自动下一曲
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    /**
     * 传感器的时间监听器
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(isShaking)return;
            if(Sensor.TYPE_ACCELEROMETER==event.sensor.getType()){
                float[] values = event.values;
                //监听3个方向上的变化,数据变化剧烈,next()播放下一曲
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

    /**
     * 更新进度的线程
     */
    private Runnable publishProgressRunnable = new Runnable() {
        @Override
        public void run() {
            while (true){
                if(player!=null&&player.isPlaying()&&musicEventListener!=null){
                    musicEventListener.onPublish(player.getCurrentPosition());
                }
                //这种sleep方式不会被Threa.interrupt()所打断
                SystemClock.sleep(100);
            }

        }
    };

    /**
     * 设置回调
     * @param listener
     */
    public void setOnMusicEventListener(OnMusicEventListener listener){
        musicEventListener =listener;
    }
    public  int getPlayingPisition(){
        return playingPisition;
    }
    /**
     * 下一曲
     * @return 当前播放位置
     */
    private int next(){

        return 1;
    }

    /**
     * 申请设备电源锁
     */
    private void acquireWakeLock(){
        MyLog.d(TAG,"正在申请电源锁");
        if(null==wakeLock){
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE,"");
            if(wakeLock!=null){
                wakeLock.acquire();
                MyLog.d(TAG,"电源申请成功");
            }
        }
    }

    /**
     * 释放设备电源锁
     */
    private void releaseWakeLock(){
        MyLog.d(TAG,"正在释放电源锁");
        if(null!=wakeLock){
            wakeLock.release();
            wakeLock=null;
            MyLog.d(TAG,"电源释放成功");
        }
    }
    /**
     * 音乐播放回调接口
     */
    public interface OnMusicEventListener{
        public void onPublish(int percent);
        public void onChange(int position);
    }
}
