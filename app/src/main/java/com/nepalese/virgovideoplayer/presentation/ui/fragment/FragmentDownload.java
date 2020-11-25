package com.nepalese.virgovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nepalese.virgosdk.Beans.M3U8;
import com.nepalese.virgosdk.Beans.M3U8Ts;
import com.nepalese.virgosdk.Util.DateUtil;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgosdk.Util.M3u8Util;
import com.nepalese.virgosdk.Util.SystemUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.data.bean.DownloadItem;
import com.nepalese.virgovideoplayer.presentation.adapter.ListView_DownloadItem_Adapter;
import com.nepalese.virgovideoplayer.presentation.component.VirgoDelIconEditText;
import com.nepalese.virgovideoplayer.presentation.helper.DownloadHelper;
import com.nepalese.virgovideoplayer.presentation.ui.DownloadDetailActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author nepalese on 2020/11/25 10:05
 * @usage 在线文件下载
 */
public class FragmentDownload extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String TAG = "FragmentDownload";

    private static final int MSG_DOWNLOAD_M3U8_OK = 1;
    private static final int MSG_DOWNLOAD_TS_OK = 2;

    private View rootView;
    private Context context;
    private DBHelper dbHelper;

    private SwipeRefreshLayout refreshLayout;
    private VirgoDelIconEditText inputM3u8, inputFile;
    private Button bM3u8, bFile;
    private ImageButton ibDetail;
    private ListView listView;
    private ListView_DownloadItem_Adapter adapter;

    private List<DownloadItem> listItem;

    private String rootPath;
    private String downloadPath;//下载文件放置位置
    private String tempPath;//m3u8 文件缓存位置
    private String m3u8Name;//m3u8 链接后部名称
    private String m3u8Url;
    private final String M3U8_CACHE_DIR = "temp";
    private final String DOWNLOAD_DIR = "download";
    private volatile int downloadNum = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download, container, false);
        context = getContext();

        init();
        setData();
        setListener();

        return rootView;
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);
        rootPath = FileUtil.getAppRootPth(context);

        refreshLayout = rootView.findViewById(R.id.swipeLayoutDownload);
        inputFile = rootView.findViewById(R.id.delInputFile);
        inputM3u8 = rootView.findViewById(R.id.delInputM3u8);

        bM3u8 = rootView.findViewById(R.id.bM3u8);
        bFile = rootView.findViewById(R.id.bFile);
        ibDetail = rootView.findViewById(R.id.ibDownloadDetail);

        listView = rootView.findViewById(R.id.listDownloadItem);

        refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);
    }

    private void setData() {
        downloadPath = rootPath + File.separator + DOWNLOAD_DIR;
        tempPath = downloadPath + File.separator + M3U8_CACHE_DIR;

        listItem = dbHelper.getAllDownloadItem();
        if(listItem==null || listItem.size()<1){
            String url = "http://pic1.win4000.com/wallpaper/2020-11-24/5fbc6eb3a2d53.jpg";
            DownloadItem downloadItem = new DownloadItem();
            downloadItem.setUrl(url);
            downloadItem.setSavePath(downloadPath);
            downloadItem.setFileName("迦娜女帝.jpg");
            dbHelper.saveDownloadItem(downloadItem);

            String url2 = "http://pic1.win4000.com/wallpaper/2020-11-23/5fbb79cba7d41.jpg";
            DownloadItem downloadItem2 = new DownloadItem();
            downloadItem2.setUrl(url2);
            downloadItem2.setSavePath(downloadPath);
            downloadItem2.setFileName(getNameWithSuffix4Url(url2));
            dbHelper.saveDownloadItem(downloadItem2);

            String url3 = "http://117.25.163.26:9990/cdmsa/2020/11/24/d9b2ff7cd598a974.mp4";
            DownloadItem downloadItem3 = new DownloadItem();
            downloadItem3.setUrl(url3);
            downloadItem3.setSavePath(downloadPath);
            downloadItem3.setFileName("哪吒预告片.MP4");
            dbHelper.saveDownloadItem(downloadItem3);
        }

        adapter = new ListView_DownloadItem_Adapter(context, listItem);
        listView.setAdapter(adapter);
    }

    private void setListener() {
        bM3u8.setOnClickListener(this);
        bFile.setOnClickListener(this);
        ibDetail.setOnClickListener(this);

        refreshLayout.setOnRefreshListener(this);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            DownloadHelper.download(listItem.get(position));
        });
    }

    @Override
    public void onRefresh() {
        updateList();
        refreshLayout.setRefreshing(false);
    }

    private void updateList() {
        listItem.clear();
        List<DownloadItem> temp = dbHelper.getAllDownloadItem();
        listItem.addAll(temp);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibDownloadDetail:
                goDownloadDetail();
                break;
            case R.id.bM3u8:
                downloadM3u8();
                break;
            case R.id.bFile:
                downloadFile();
                break;
        }
    }

    private void goDownloadDetail() {
        Intent intent = new Intent(context, DownloadDetailActivity.class);
        startActivity(intent);
    }

    private void downloadM3u8() {
        m3u8Url = inputM3u8.getText().toString().trim();
        if(!TextUtils.isEmpty(m3u8Url) && (m3u8Url.startsWith("http") || m3u8Url.startsWith("https")) && m3u8Url.endsWith("m3u8")){
            //显示下载提示
            bM3u8.setEnabled(false);
            m3u8Name  =  M3u8Util.getM3u8Name(m3u8Url);
            File tempDir = new File(tempPath);
            if (tempDir.exists()) {
                // 清空内部文件
                FileUtil.deleteDir(tempDir.getAbsolutePath());
            }

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    File dir = new File(tempPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    InputStream ireader;
                    try {
                        ireader = new URL(m3u8Url).openStream();
                        FileOutputStream writer = new FileOutputStream(new File(dir, m3u8Name));
                        FileUtil.readerWriterStream(ireader, writer);
                        handler.sendEmptyMessage(MSG_DOWNLOAD_M3U8_OK);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            SystemUtil.showToast(context, "无效链接！");
        }
    }

    private void parseDownloadTS() {
        M3U8 m3u8 = M3u8Util.parseM3u8File(tempPath, m3u8Name, m3u8Url);
        downloadFragment(m3u8);
    }

    private void downloadFragment(M3U8 m3u8) {
        downloadNum = 0;
        File dir = new File(m3u8.getSaveDir());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (M3U8 m : m3u8.getM3u8List()) {
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
            };
            new Thread(runnable).start();
        }
    }

    private void downloadFile() {
        String url = inputFile.getText().toString().trim();
        if(!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))){
            DownloadItem downloadItem = new DownloadItem();
            downloadItem.setUrl(url);
            downloadItem.setSavePath(downloadPath);
            downloadItem.setFileName(getNameWithSuffix4Url(url));
            dbHelper.saveDownloadItem(downloadItem);
            DownloadHelper.download(downloadItem);
        }
    }

    private String getName4Url(String url){
        if(TextUtils.isEmpty(url)){
            return "";
        }

        return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".") );
    }

    private String getNameWithSuffix4Url(String url){
        if(TextUtils.isEmpty(url)){
            return "";
        }

        return url.substring(url.lastIndexOf("/") + 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_DOWNLOAD_M3U8_OK:
                    parseDownloadTS();
                    break;
                case MSG_DOWNLOAD_TS_OK:
                    M3u8Util.mergeTsFile((M3U8)msg.obj, downloadPath, DateUtil.getCurTime()+".mp4");
                    Log.i(TAG, "handleMessage: merge success!");
                    //清除缓存
                    FileUtil.deleteDir(tempPath);
                    bM3u8.setEnabled(true);
                    break;
            }
        }
    };
}
