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
import com.nepalese.virgovideoplayer.presentation.manager.parseM3U8;
import com.nepalese.virgovideoplayer.presentation.ui.DownloadDetailActivity;
import com.nepalese.virgovideoplayer.presentation.manager.parseUrl;

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

    private static final int MSG_FLASH_LIST = 1;

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
        listItem = dbHelper.getAllDownloadItem();

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

    private void downloadM3u8(){
        inputM3u8.setEnabled(false);
        String m3u8Url = inputM3u8.getText().toString().trim();
        new Thread(){
            @Override
            public void run() {
                super.run();
                int out = parseM3U8.getInstance(context).downloadM3u8(m3u8Url);
                Log.i(TAG, "run: " + out);
                inputM3u8.setEnabled(true);
            }
        }.start();
    }

    private void downloadFile() {
        String url = inputFile.getText().toString().trim();
        if(!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))) {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Log.i(TAG, "run: ");
                    List<DownloadItem> temp = parseUrl.getInstance(context).getDownloadItemList(url);
                    for(DownloadItem item: temp){
                        dbHelper.saveDownloadItem(item);
                    }
                    handler.sendEmptyMessage(MSG_FLASH_LIST);
                }
            }.start();
        }else{
            SystemUtil.showToast(context, "无效链接！");
        }
    }

    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_FLASH_LIST:
                    updateList();
                    break;
            }
        }
    };
}
