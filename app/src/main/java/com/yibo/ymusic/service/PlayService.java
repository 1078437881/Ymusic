package com.yibo.ymusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import com.yibo.ymusic.utils.imageLoder.ImageLoader;
import com.yibo.ymusic.utils.imageLoder.ImageTools;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/4/13.
 * 音乐播放服务
 * 功能:摇一摇播放下一曲
 */

public class PlayService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = PlayService.class.getSimpleName();
    private SensorManager sensorManager;//传感器
    private MediaPlayer player;
    private OnMusicEventListener musicEventListener;
    private int playingPisition = 0;//当前播放位置
    private PowerManager.WakeLock wakeLock = null;//获取设备电源锁,防止锁屏后服务停止
    private boolean isShaking;
    private Notification notification;//通知栏
    private RemoteViews remoteViews;//通知栏布局
    private NotificationManager notificationManager;

    //单线程池
    private ExecutorService progressUpdatedListener = Executors.newSingleThreadExecutor();

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();


        acquireWakeLock();//获取设备电源锁
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


//        MusicUtils.intitMusicList();
        playingPisition = (Integer) SpUtils.get(this, Constants.PLAY_POS, 0);

        if (MusicUtils.musicArrayList.size() <= 0) {
            Toast.makeText(getApplicationContext(), "当前手机没有MP3文件", Toast.LENGTH_SHORT).show();
        } else {
            if (getPlayingPisition() < 0) {
                playingPisition = 0;
            }
            Uri uri = Uri.parse(MusicUtils.musicArrayList.get(getPlayingPisition()).getUri());
            player = MediaPlayer.create(PlayService.this, uri);
            player.setOnCompletionListener(this);
        }
        //开始更新进度的线程
        progressUpdatedListener.execute(publishProgressRunnable);

        //该方法虽然被抛弃过时,但是通用
        PendingIntent pendingIntent = PendingIntent.getActivity(PlayService.this, 0,
                new Intent(PlayService.this, PlayerActivity.class), 0);
        remoteViews = new RemoteViews(getPackageName(), R.layout.play_notifination);
        notification = new Notification(R.drawable.ic_launcher_background, "歌曲正在播放", System.currentTimeMillis());
        notification.contentIntent = pendingIntent;
        notification.contentView = remoteViews;
        //标记位,设置通知栏一直存在
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        Intent intent = new Intent(PlayService.class.getSimpleName());
        intent.putExtra("BUTTON_NF", 1);//上一曲
        PendingIntent preIntent = PendingIntent.getBroadcast(
                PlayService.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.nf_previous, preIntent);

        intent.putExtra("BUTTON_NF", 2);//暂停/播放
        PendingIntent pauseIntent = PendingIntent.getBroadcast(
                PlayService.this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.nf_play, pauseIntent);

        intent.putExtra("BUTTON_NF", 3);//下一曲
        PendingIntent netxIntent = PendingIntent.getBroadcast(
                PlayService.this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.nf_next, netxIntent);

        intent.putExtra("BUTTON_NF", 4);//退出通知栏
        PendingIntent exitIntent = PendingIntent.getBroadcast(
                PlayService.this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.nf_exit, exitIntent);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setRemoteViews();

        IntentFilter filter = new IntentFilter(PlayService.class.getSimpleName());
        MyBroadCastReceiver receiver = new MyBroadCastReceiver();
        registerReceiver(receiver, filter);


    }

    public void setRemoteViews() {
        MyLog.d(TAG, "--->setRemoteViews");
        if (getPlayingPisition() > 0) {
            remoteViews.setTextViewText(R.id.nf_title, MusicUtils.musicArrayList.get(getPlayingPisition()).getTitle());
            remoteViews.setTextViewText(R.id.nf_artist, MusicUtils.musicArrayList.get(getPlayingPisition()).getArtist());
            Bitmap icon = ImageLoader.getInstance().load(MusicUtils.musicArrayList.get(getPlayingPisition()).getImage());

            remoteViews.setImageViewBitmap(R.id.nf_picture,
                    icon == null ? ImageTools.scaleBitmap(R.drawable.ic_launcher_background) : ImageTools.scaleBitmap(icon));
        }else{
            remoteViews.setImageViewBitmap(R.id.nf_picture,ImageTools.scaleBitmap(R.mipmap.ic_launcher));
//            remoteViews.setTextViewText(R.id.nf_title, "歌曲");
//            remoteViews.setTextViewText(R.id.nf_artist, "歌手");
        }
        remoteViews.setImageViewBitmap(R.id.nf_previous,ImageTools.scaleBitmap(R.drawable.ic_media_previous));
        remoteViews.setImageViewBitmap(R.id.nf_next,ImageTools.scaleBitmap(R.drawable.ic_media_next));
        remoteViews.setImageViewBitmap(R.id.nf_exit,ImageTools.scaleBitmap(R.drawable.ic_delete));
        if (isPlaying()) {
            remoteViews.setImageViewResource(R.id.nf_play, R.drawable.ic_media_pause);
        } else {
            remoteViews.setImageViewResource(R.id.nf_play, R.drawable.ic_media_play);
        }


        //通知栏更新
        notificationManager.notify(5, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(0, notification);//让服务前台运行
        return Service.START_STICKY;
    }


    /**
     * 传感器的时间监听器
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isShaking) return;
            if (Sensor.TYPE_ACCELEROMETER == event.sensor.getType()) {
                float[] values = event.values;
                //监听3个方向上的变化,数据变化剧烈,next()播放下一曲
                if (Math.abs(values[0]) > 8 && Math.abs(values[1]) > 8 && Math.abs(values[2]) > 8) {
                    isShaking = true;
                    next();
                    //延迟200毫秒,防止抖动
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isShaking = false;
                        }
                    }, 200);
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
            while (true) {
                if (player != null && player.isPlaying() && musicEventListener != null) {
                    musicEventListener.onPublish(player.getCurrentPosition());
                }
                //这种sleep方式不会被Threa.interrupt()所打断
                SystemClock.sleep(100);
            }

        }
    };

    /**
     * 设置回调
     *
     * @param listener
     */
    public void setOnMusicEventListener(OnMusicEventListener listener) {
        musicEventListener = listener;
    }

    public int play(int position) {
        MyLog.d(TAG, "--->play(int position methed");
        if (MusicUtils.musicArrayList.size() <= 0) {
            Toast.makeText(getApplicationContext(), "当前手机没有MP3文件", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (position < 0)
            position = 0;
        if (position >= MusicUtils.musicArrayList.size())
            position = position % MusicUtils.musicArrayList.size();
        try {
            player.reset();
            player.setDataSource(MusicUtils.musicArrayList.get(position).getUri());
            player.prepare();

            player.start();
            if (musicEventListener != null) {
                musicEventListener.onChange(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        playingPisition = position;
        SpUtils.put(Constants.PLAY_POS, playingPisition);
        setRemoteViews();
        return playingPisition;

    }


    /**
     * 获取正在播放的歌曲列表位置
     *
     * @return
     */
    public int getPlayingPisition() {
        return playingPisition;
    }

    /**
     * 播放完毕自动下一曲
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    /**
     * 继续播放
     *
     * @return 当前播放的位置 默认为0
     */
    public int resume() {
        if (player == null) {
            return -1;
        }
        if (isPlaying()) {
            return -1;
        }
        player.start();
        setRemoteViews();
        return playingPisition;
    }

    /**
     * 暂停播放
     *
     * @return 当前播放位置
     */
    public int pause() {
        if (MusicUtils.musicArrayList.size() == 0) {
            Toast.makeText(getApplicationContext(), "当前手机没有MP3文件", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (!isPlaying()) {
            return -1;
        }
        player.pause();
        setRemoteViews();
        return playingPisition;
    }

    /**
     * 下一曲
     *
     * @return 当前播放位置
     */
    private int next() {
        if (playingPisition >= MusicUtils.musicArrayList.size() - 1) {
            return play(0);
        }
        return play(playingPisition + 1);
    }

    /**
     * 上一曲
     *
     * @return 当前播放位置
     */
    public int previous() {
        if (playingPisition <= 0) {
            return play(MusicUtils.musicArrayList.size() - 1);
        }
        return play(playingPisition - 1);
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return null != player && player.isPlaying();
    }

    /**
     * 开始播放
     */
    private void start() {
        player.start();
    }

    /**
     * 获取当前正在播放的音乐总时长
     *
     * @return
     */
    public int getDuration() {
        if (!isPlaying()) {
            return 0;
        }
        return player.getDuration();
    }

    /**
     * 拖动到指定位置进行播放
     *
     * @param msec
     */
    public void seek(int msec) {
        if (!isPlaying())
            return;
        player.seekTo(msec);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        MyLog.d(TAG, "unbind");
        sensorManager.unregisterListener(sensorEventListener);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (musicEventListener != null)
            musicEventListener.onChange(playingPisition);
    }

    @Override
    public void onDestroy() {
        MyLog.d(TAG, "--->onDestroy()");
        stopForeground(true);
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }

    /**
     * 服务销毁时,释放各种控件
     */
    private void release() {
        if (progressUpdatedListener.isShutdown())
            progressUpdatedListener.shutdownNow();
        progressUpdatedListener = null;
        //释放设备电源锁
        releaseWakeLock();
        if (player != null)
            player.release();
        player = null;

    }

    /**
     * 申请设备电源锁
     */
    private void acquireWakeLock() {
        MyLog.d(TAG, "正在申请电源锁");
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "");
            if (wakeLock != null) {
                wakeLock.acquire();
                MyLog.d(TAG, "电源申请成功");
            }
        }
    }

    /**
     * 释放设备电源锁
     */
    private void releaseWakeLock() {
        MyLog.d(TAG, "正在释放电源锁");
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
            MyLog.d(TAG, "电源释放成功");
        }
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayService.class.getSimpleName())) {
                MyLog.d(TAG, "MyBroadCastReceiver--->onReceive()");
                MyLog.d(TAG, "button_noti-->" + intent.getIntExtra("BUTTON_NOTI", 0));
                switch (intent.getIntExtra("BUTTON_NOTI", 0)) {
                    case 1:
                        previous();
                        break;
                    case 2:
                        if (isPlaying()) {
                            pause();//暂停
                        } else {
                            resume();//播放
                        }
                        break;
                    case 3:
                        next();
                    case 4:
                        if (isPlaying()) {
                            pause();
                        }
                        //取消通知栏
                        notificationManager.cancel(5);
                        break;
                    default:
                        break;
                }
                if (musicEventListener != null) {
                    musicEventListener.onChange(getPlayingPisition());
                }
            }
        }
    }

    /**
     * 音乐播放回调接口
     */
    public interface OnMusicEventListener {
        public void onPublish(int percent);

        public void onChange(int position);
    }

}
