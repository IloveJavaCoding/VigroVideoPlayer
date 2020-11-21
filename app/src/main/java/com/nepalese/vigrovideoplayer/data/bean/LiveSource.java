package com.nepalese.vigrovideoplayer.data.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author nepalese on 2020/11/20 18:00
 * @usage
 */

@Entity
public class LiveSource {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String url;
    private String name;
    @Generated(hash = 1387624386)
    public LiveSource(Long id, String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }
    @Generated(hash = 82903220)
    public LiveSource() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LiveSource{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
