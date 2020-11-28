package com.nepalese.virgovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nepalese.virgosdk.Beans.M3U8;
import com.nepalese.virgosdk.Beans.M3U8Ts;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgosdk.Util.M3u8Util;
import com.nepalese.virgosdk.Util.SystemUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.presentation.helper.DownloadHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentSetting extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentSetting";
    
    private static final int MSG_DOWNLOAD_FILE_OK = 1;
    private static final int MSG_DOWNLOAD_TS_OK = 2;

    private View rootView;
    private Context context;
    private DBHelper dbHelper;

    private final String url = "http://183.207.249.36:80/PLTV/4/224/3221227387/index.m3u8";
    private final String saveName = "cctv13.mp4";
    private final String temp = "temp";
    private String saveDir;
    private String tempPath;
    private String m3u8Name;

    private volatile int downloadNum = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        context = getContext();

        init();
        setData();

        return rootView;
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);

        rootView.findViewById(R.id.bClearAll).setOnClickListener(this);
        rootView.findViewById(R.id.bClearLocal).setOnClickListener(this);
        rootView.findViewById(R.id.bClearOnline).setOnClickListener(this);
        rootView.findViewById(R.id.bClearDownload).setOnClickListener(this);
        rootView.findViewById(R.id.bClearTask).setOnClickListener(this);

        rootView.findViewById(R.id.bDownload).setOnClickListener(this);
    }

    private void setData() {
        saveDir = FileUtil.getRootPath();
        tempPath = saveDir + File.separator + temp;//存放缓存文件
        m3u8Name =  M3u8Util.getM3u8Name(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bClearAll:
                dbHelper.clearAllTable();
                break;
            case R.id.bClearLocal:
                dbHelper.clearLocalVideo();
                break;
            case R.id.bClearOnline:
                dbHelper.clearLiveSource();
                break;
            case R.id.bClearDownload:
                dbHelper.clearDownloadItem();
                break;
            case R.id.bClearTask:
                DownloadHelper.clearAllTask();
                break;
            case R.id.bDownload:
                download();
                break;
        }
    }

    private void download() {
        File tempDir = new File(tempPath);
        if (tempDir.exists()) {
            // 清空内部文件
            FileUtil.deleteDir(tempDir.getAbsolutePath());
        }
        
        new Thread(){
            @Override
            public void run() {
                super.run();
                //M3u8Util.saveM3u8File(url, tempPath, m3u8Name);
                File dir = new File(tempPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                InputStream ireader;
                try {
                    ireader = new URL(url).openStream();
                    FileOutputStream writer = new FileOutputStream(new File(dir, m3u8Name));
                    FileUtil.readerWriterStream(ireader, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(MSG_DOWNLOAD_FILE_OK);
            }
        }.start();
    }
    
    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_DOWNLOAD_FILE_OK:
                    parseAndDownTs();
                    break;
                case MSG_DOWNLOAD_TS_OK:
                    M3u8Util.mergeTsFile((M3U8)msg.obj, saveDir, saveName);
                    Log.i(TAG, "handleMessage: merge success!");
                    //清除缓存
                    FileUtil.deleteDir(tempPath);
                    break;
            }
        }
    };

    private void parseAndDownTs() {
        M3U8 m3u8 = M3u8Util.parseM3u8File(tempPath, m3u8Name, url);
        downloadFragment(m3u8);
    }

    private void downloadFragment(M3U8 m3u8) {
        File dir = new File(m3u8.getSaveDir());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (M3U8 m : m3u8.getM3u8List()) {
            // 下载对应的m3u8
            downloadFragment(m);
        }

        for (M3U8Ts ts : m3u8.getTsList()) {
            Runnable runnable = () -> {
                FileOutputStream writer;
                try {
                    writer = new FileOutputStream(new File(dir, ts.getContent()));
                    FileUtil.readerWriterStream(new URL(m3u8.getUrlRefer() + ts.getContent()).openStream(), writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ++downloadNum;
                Log.i(TAG, "parseAndDownTs: " + downloadNum);
                if(downloadNum>=m3u8.getTsList().size()){
                    Message message = Message.obtain();
                    message.what = MSG_DOWNLOAD_TS_OK;
                    message.obj = m3u8;
                    handler.sendMessage(message);
                }
                Log.i(TAG, "parseAndDownTs: save ts: " + ts.getContent());
            };
            new Thread(runnable).start();
        }
    }
}
