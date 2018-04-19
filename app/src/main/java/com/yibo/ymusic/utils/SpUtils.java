package com.yibo.ymusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yibo.ymusic.application.MusicApp;

/**
 * Created by Administrator on 2018/4/18.
 *
 * SharredPreferences utils
 */

public class SpUtils {

    public static void put(final String key,final Object value){
        SharedPreferences sharedPreferences = MusicApp.aCOntext.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(value instanceof Integer){
            editor.putInt(key,(Integer)value);
        }else if(value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }else if(value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }else if(value instanceof Long) {
            editor.putLong(key, (Long) value);
        }else {
            editor.putString(key, (String) value);
        }

        editor.commit();
    }

    public static Object get(Context cOntext,String key,Object object){
        SharedPreferences sharedPreferences = MusicApp.aCOntext.getSharedPreferences(Constants.SP_NAME,Context.MODE_PRIVATE);

        if(object instanceof String ){
            return sharedPreferences.getString(key,(String) object);
        } else if (object instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) object);
        } else if (object instanceof Long) {
            return sharedPreferences.getLong(key, (Long) object);
        }
        return object;
    }

    /**
     * 移除key值对应的数据
     * @param context
     * @param key
     */
    public static void remove(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SP_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).commit();
    }

    /**
     * 清楚所有数据
     * @param context
     */
    public static void clear(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }
}
