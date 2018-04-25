package com.yibo.ymusic.utils.imageLoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.yibo.ymusic.application.MusicApp;

/**
 * Created by Administrator on 2018/4/24.
 */

public class ImageTools {


    /**
     * 图片缩放,默认
     * @param bitmap
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap){
        return scaleBitmap(bitmap,(int)(MusicApp.screenWith*0.13));
    }


    /**
     * 图片缩放
     * @param bitmap
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap,int size){
        return Bitmap.createScaledBitmap(bitmap,size,size,true);
    }

    /**
     * 根据uri缩放图片
     * @param uri
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(String uri,int size){
        return scaleBitmap(BitmapFactory.decodeFile(uri),size);
    }

    /**
     * 根据uri缩放图片,默认
     * @param uri
     * @return
     */
    public static Bitmap scaleBitmap(String uri){
        return scaleBitmap(BitmapFactory.decodeFile(uri));
    }

    /**
     * 缩放资源图片
     * @param res
     * @return
     */
    public static Bitmap scaleBitmap(int res){
        return scaleBitmap(BitmapFactory.decodeResource(MusicApp.aCOntext.getResources(),res));
    }

    /**
     * 创建圆形图片
     * @param src
     * @return
     */
    private static Bitmap createCircleBitmap(Bitmap src){

        int size = (int) (MusicApp.screenWith*0.13);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setARGB(255,241,239,229);

        Bitmap target = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(size/2,size/2,size/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(src,0,0,paint);

        return  target;
    }

    private static Bitmap createCircleBitmap(String uri){
        return createCircleBitmap(BitmapFactory.decodeFile(uri));
    }

    private static Bitmap createCircleBitmap(int res){
        return createCircleBitmap(BitmapFactory.decodeResource(MusicApp.aCOntext.getResources(),res));
    }
}
