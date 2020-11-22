package com.nepalese.virgovideoplayer.data.bean;

import com.nepalese.virgosdk.Beans.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author nepalese on 2020/10/27 16:24
 * @usage
 */
@Entity
public class DownloadItem extends BaseBean {
    public static final int DOWNLOAD_SUCCESS = 1;
    public static final int DOWNLOAD_FAIL = -1;
    public static final int DOWNLOAD_DO = 0;

    @Id(autoincrement = true)
    private Long id;
    
    @Unique
    private String url;
    private String fileName;
    private String savePath;
    @Generated(hash = 1146447522)
    public DownloadItem(Long id, String url, String fileName, String savePath) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.savePath = savePath;
    }
    @Generated(hash = 187637005)
    public DownloadItem() {
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
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getSavePath() {
        return this.savePath;
    }
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public String toJson() {
        return super.toJson();
    }
}