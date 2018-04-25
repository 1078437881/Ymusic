package com.yibo.ymusic.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.yibo.ymusic.application.MusicApp;
import com.yibo.ymusic.beans.Music;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/17.
 */

public class LocalMusicUtils {

    /**
     * 根据id获取歌曲uri
     *
     * @param musicid
     * @return
     */
    public static String queryMusicById(int musicid) {
        String result = null;
        Cursor cursor = MusicApp.aCOntext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media._ID + "=?",
                new String[]{String.valueOf(musicid)},
                null
        );
        for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
            result = cursor.getString(0);
            break;
        }
        cursor.close();
        return result;
    }

    /**
     * 获取目录下的歌曲
     *
     * @param dirName
     * @return
     */
    public static ArrayList<Music> queryMusic(String dirName) {
        ArrayList<Music> results = new ArrayList<Music>();
        Cursor cursor = MusicApp.aCOntext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{dirName + "%"},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        if (cursor == null) {
            cursor = MusicApp.aCOntext.getContentResolver().query(
                    MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Media.DATA + " like ?",
                    new String[]{dirName + "%"},
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER
            );
            if (cursor == null) {
                return results;
            }
        }
        //id title singer data time image
        addMedia(cursor, results);

        cursor = MusicApp.aCOntext.getContentResolver().query(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{dirName + "%"},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER

        );
        if (cursor == null) {
            return results;
        }
        addMedia(cursor, results);
        return results;
    }

    private static void addMedia(Cursor cursor, ArrayList<Music> results) {
        Music music;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //若不是音乐文件,不加入List
            String isMusic = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != null && isMusic.equals("")) continue;

            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            if(isRepeat(title,artist))continue;

            music = new Music();
            music.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            music.setTitle(title);
            music.setArtist(artist);
            music.setUri(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            music.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            music.setImage(getAlbumImage(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));

            results.add(music);
        }
        cursor.close();
    }

    /**
     * 根据音乐名称和艺术家来判断是否重复
     * @param title
     * @param artist
     * @return
     */
    private static boolean isRepeat(String title, String artist) {
        for (Music music : MusicUtils.musicArrayList) {
            if (title.equals(music.getTitle()) && artist.equals(music.getArtist())) {
                return true;
            }
        }
        return true;
    }

    /**
     * 根据歌曲id获取图片
     * @param albumId
     * @return
     */
    private static String getAlbumImage(int albumId){
        String result = "";
        Cursor cursor = null;
        try {
            cursor = MusicApp.aCOntext.getContentResolver().query(
                    Uri.parse("content://media/external/audio/albums/"+albumId),
                    new String[]{"album_art"},
                    null,null,null
            );
            for (cursor.moveToFirst(); !cursor.isAfterLast();) {
                result = cursor.getString(0);
                break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return result;
    }
}
