package com.yibo.ymusic.beans;

/**
 * Created by Administrator on 2018/4/16.
 *
 * data of Music
 */

public class Music {

    private int id;
    private String title;//音乐标题
    private String uri;//音乐路径
    private int length;//长度
    private String image;//icon
    private String artist;//艺术家

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
