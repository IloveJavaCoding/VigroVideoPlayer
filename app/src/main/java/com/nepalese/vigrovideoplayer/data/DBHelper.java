package com.nepalese.vigrovideoplayer.data;

import android.content.Context;

import com.nepalese.vigrovideoplayer.data.bean.DownloadItem;
import com.nepalese.vigrovideoplayer.data.bean.Video;
import com.nepalese.vigrovideoplayer.data.db.DaoMaster;
import com.nepalese.vigrovideoplayer.data.db.DaoSession;
import com.nepalese.vigrovideoplayer.data.db.DownloadItemDao;
import com.nepalese.vigrovideoplayer.data.db.VideoDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


/**
 * @author nepalese on 2020/10/27 16:39
 * @usage
 */
public class DBHelper {
    private static final String TAG = "DBHelper";

    private Context mContext;
    private static volatile DBHelper instance;

    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private VideoDao videoDao;
    private DownloadItemDao downloadItemDao;

    private DBHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context not null");
        }
        mContext = context;
        DaoSession session = getDaoSession(context);

        videoDao = session.getVideoDao();
        downloadItemDao = session.getDownloadItemDao();
    }

    private DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            daoMaster = getDaoMaster(context);
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    private DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DatabaseOpenHelper helper = new DatabaseOpenHelper(context, DatabaseOpenHelper.DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDb());
        }
        return daoMaster;
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    //===========================================增删改查====================================
    //video dao:
    public void saveVideo(Video item){
        videoDao.insert(item);
    }

    public void deleteVideo(Video item){
        videoDao.delete(item);
    }

    public void clearVideo(){
        videoDao.deleteAll();
    }

    public void deleteVideoByName(String name){
        List<Video> list = getVideoByName(name);
        for(Video video: list){
            videoDao.deleteByKey(video.getId());
        }
    }

    public void updateVideo(Video item){
       videoDao.update(item);
    }

    public Video getVideo(long id){
        return videoDao.load(id);
    }

    public List<Video> getVideoByName(String name){
        QueryBuilder<Video> qb = videoDao.queryBuilder().where(VideoDao.Properties.Name.eq(name));
        return qb.build().list();
    }

    public List<Video> getAllVideo(){
        return videoDao.loadAll();
    }

    //downloaditem dao:
    public void saveDownloadItem(DownloadItem item){
        downloadItemDao.insert(item);
    }

    public void deleteDownloadItem(DownloadItem item){
        downloadItemDao.delete(item);
    }

    public void updateDownloadItem(DownloadItem item){
        downloadItemDao.update(item);
    }

    public List<DownloadItem> getAllDownloadItem(){
        return downloadItemDao.loadAll();
    }
}
