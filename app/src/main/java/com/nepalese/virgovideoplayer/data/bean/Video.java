package com.nepalese.virgovideoplayer.data.bean;

import com.nepalese.virgosdk.Beans.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author nepalese on 2020/10/27 16:02
 * @usage
 */
@Entity
public class Video extends BaseBean {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String name;
    private String artist;
    private String path;
    private String thumbPath;
    private String resolution;
    private long size;
    private long date;
    private long duration;
    @Generated(hash = 711597480)
    public Video(Long id, String name, String artist, String path, String thumbPath,
            String resolution, long size, long date, long duration) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.path = path;
        this.thumbPath = thumbPath;
        this.resolution = resolution;
        this.size = size;
        this.date = date;
        this.duration = duration;
    }
    @Generated(hash = 237528154)
    public Video() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getArtist() {
        return this.artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getThumbPath() {
        return this.thumbPath;
    }
    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
    public String getResolution() {
        return this.resolution;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public long getDuration() {
        return this.duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toJson() {
        return super.toJson();
    }
}
