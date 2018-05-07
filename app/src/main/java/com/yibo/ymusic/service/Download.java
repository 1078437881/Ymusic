package com.yibo.ymusic.service;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/28.
 */

public class Download implements Serializable{
    private static final long serialVersionUID = 0x00001000L;
    private static final int START = 1;    //开始下载
    private static final int PUBLISH = 2;  //更新进度
    private static final int PAUSE = 3;    //暂停下载
    private static final int CANCEL = 4;   //取消下载
    private static final int ERROR = 5;    //下载错误
    private static final int SUCCESS = 6;  //下载成功
    private static final int GOON = 7;     //继续下载
}
