package com.yibo.ymusic.utils.imageLoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.yibo.ymusic.utils.Encrypt;

/**
 * Created by Administrator on 2018/4/23.
 */

public class ImageLoader {

    private static ImageLoader instance;

    private LruCache<String,Bitmap> mCache;

    //获取MusicIconLoader的实例
    public synchronized static ImageLoader getInstance(){
        if(instance ==null){
            instance = new ImageLoader();
        }
        return instance;
    }

    //构造方法,初始化LruCache
    private ImageLoader(){
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
        mCache = new LruCache<String,Bitmap>(maxSize){
            protected int sizeOf(String key,Bitmap value){
                return value.getByteCount();
            }
        };
    }

    public Bitmap load(final String uri){
        if(uri == null){
            return null;
        }
        final String key = Encrypt.md5(uri);
        Bitmap bitmap = getFromCache(key);

        if (bitmap !=null) return bitmap;

        bitmap = BitmapFactory.decodeFile(uri);
        addToCache(key,bitmap);
        return bitmap;

    }

    //从内存中获取图片
    private Bitmap getFromCache(final String key){
        return mCache.get(key);
    }

    //将图片缓存到内存中
    private void addToCache(final String key,final Bitmap bitmap){
        if(getFromCache(key)==null&&key!=null&&bitmap!=null)
            mCache.put(key,bitmap);
    }
}
