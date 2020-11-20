package com.nepalese.vigrovideoplayer.presentation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nepalese.vigrovideoplayer.data.Constants;
import com.nepalese.vigrovideoplayer.data.DBHelper;
import com.nepalese.vigrovideoplayer.data.bean.Video;
import com.nepalese.vigrovideoplayer.presentation.component.FloatView;
import com.nepalese.vigrovideoplayer.presentation.event.FinishScanEvent;
import com.nepalese.vigrovideoplayer.presentation.event.StartScanVideoEvent;
import com.nepalese.virgosdk.Util.BitmapUtil;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgosdk.Util.MediaUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @author nepalese on 2020/10/29 08:51
 * @usage
 */
public class NetworkService extends Service {
    private static final String TAG = "NetworkService";

    private Context context;
//    private FloatView floatView;//自定义提示框
    private VirgoHandler handler;
    private DBHelper dbHelper;

    private String thumbPath;

    public static Intent getIntent(Context context, String action, String extras) {
        Intent intent = new Intent();
        intent.setClass(context, NetworkService.class);
        intent.putExtra("action", action);
        intent.putExtra("data", extras);
        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new VirgoHandler(this);
        dbHelper = DBHelper.getInstance(context);
        thumbPath = FileUtil.getAppRootPth(context) + File.separator + Constants.DIR_THUMB_NAIL;

        registerEventBus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent == null ? null : intent.getExtras();
        if (extras != null) {
            doCheck(extras.getString("action"), extras.getString("data"));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
    }

    //=================================================================================
    private void doCheck(String action, String data) {
        if (TextUtils.isEmpty(action)) return;
        switch (action){
            case Constants.ACTION_START_HOME:
                startHome();
                break;
        }
    }

    private void startHome() {

    }

    //file is directory
    private void scanVideoFile(File file){
        if(!file.isDirectory()){
            return;
        }
        File[] f1 = file.listFiles();
        if(f1!=null && f1.length>0){
            for(File f2: f1){
                if(f2.isFile()){
                    for(String post: Constants.VIDEO_EXTENSION){
                        if(f2.getPath().endsWith(post)){
                            Log.i(TAG, "scanVideoFile: " + f2.getAbsolutePath());
                            Video video = getVideoFileInfo(context, f2.getAbsolutePath());
                            //1. 获取缩略图
                            //2. 进行压缩
                            //3. 保存到本地
                            BitmapUtil.saveBitmap2File(BitmapUtil.compressBitmap(MediaUtil.getVideoThumb(context, f2.getAbsolutePath(), 1),100), thumbPath, video.getName() + ".jpg");
                            dbHelper.saveVideo(video);
                        }
                    }
                }else{
                    scanVideoFile(f2);
                }
            }
        }
    }

    private Video getVideoFileInfo(Context context, String path) {
        Video videoFile = null;
        File file = new File(path);
        if (file.exists()) {
            String displayName = path.substring(path.lastIndexOf('/') + 1);
            String name = displayName.substring(0, displayName.lastIndexOf('.'));
            videoFile = new Video();
            videoFile.setName(name);
            videoFile.setArtist("UnKnown");
            videoFile.setPath(path);
            videoFile.setResolution(MediaUtil.getVideoResolution(context, path, 1));
            videoFile.setThumbPath(thumbPath + File.separator + name + ".jpg");
            videoFile.setSize(file.length());
            videoFile.setDate(System.currentTimeMillis());
            videoFile.setDuration(MediaUtil.getDuration(path));
        }

        return videoFile;
    }

    //==================================================================================
    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void post(Object object) {
        EventBus.getDefault().post(object);
    }

    @Subscribe
    public void onMainThread(StartScanVideoEvent event){
        for(File file: event.getList()) {
            scanVideoFile(file);
        }
        post(new FinishScanEvent());
    }

    //======================================================================================
    private static class VirgoHandler extends Handler{
        WeakReference<NetworkService> reference;

        public VirgoHandler(NetworkService networkService){
            reference = new WeakReference<>(networkService);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            NetworkService networkService = reference.get();
            if(networkService!=null){
                switch (msg.what){
                }
            }
        }
    }
}
