package com.yibo.ymusic.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2018/4/23.
 *
 * MD5加密
 */

public class Encrypt {

    public synchronized static String md5(String str){
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(str.getBytes("UTF-8"));
            byte[] hash = md.digest();
            int len = hash.length;
            StringBuffer sb = new StringBuffer();
            for(int i =0;i<len;i++){
                if(1==Integer.toHexString(0xFF&hash[i]).length()){
                    sb.append(0);
                }
                sb.append(Integer.toHexString(0xFF&hash[i]));
            }
            return sb.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }
}
