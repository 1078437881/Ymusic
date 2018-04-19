package com.yibo.ymusic.utils;

import android.os.Environment;

import com.yibo.ymusic.application.MusicApp;
import com.yibo.ymusic.beans.Music;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/16.
 */

public class MusicUtils {


    private static String APP_DIR = "ymusic";
    //存放歌曲列表
    public static ArrayList<Music> musicArrayList = new ArrayList<Music>();


    public static void intitMusicList(){
        //获取歌曲列表
        musicArrayList.clear();
        musicArrayList.addAll(LocalMusicUtils.queryMusic(getBaseDir()));
    }

    /**
     * 获取内存卡根目录
     * @return
     */
    public static String getBaseDir(){
        String dir = null;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)){
            dir = Environment.getExternalStorageDirectory()+ File.separator;
        }else{
            dir = MusicApp.aCOntext.getFilesDir()+File.separator;
        }
        return dir;
    }

    /**
     * 创建获取应用程序使用的本地目录
     * @return
     */
    public static String getAPPLocalDir(){
        String dir = null;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)){
            dir = Environment.getExternalStorageDirectory()+File.separator;
        }
        return mkdirs(dir);
    }

    /**
     * 创建获取音乐存放目录
     * @return
     */
    public static String getMusicDir(){
        String musicDir = getAPPLocalDir()+"music"+File.separator;
        return mkdirs(musicDir);
    }

    public static String getLrcDir(){
        String lrcDir = getAPPLocalDir()+"lrc"+File.separator;
        return mkdirs(lrcDir);
    }

    /**
     * 创建文件夹
     * @param dir
     * @return
     */
    public static String mkdirs(String dir){
        File file = new File(dir);
        if(!file.exists()){
          for(int i = 0;i<5;i++){
              if(file.mkdirs())
                  return dir;
          }
        }
        return dir;
    }
}
