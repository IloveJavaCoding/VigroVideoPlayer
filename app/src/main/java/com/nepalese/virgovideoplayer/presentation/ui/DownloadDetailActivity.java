package com.nepalese.virgovideoplayer.presentation.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadTask;
import com.nepalese.virgosdk.Base.BaseActivity;
import com.nepalese.virgosdk.Util.PathUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.presentation.adapter.ListView_DownloadTask_Adapter;
import com.nepalese.virgovideoplayer.presentation.helper.DownloadHelper;

import java.io.File;
import java.util.List;

public class DownloadDetailActivity extends BaseActivity implements ListView_DownloadTask_Adapter.downloadTaskInterface {
    private static final String TAG = "DownloadDetailActivity";

    private ImageButton iBack;
    private TextView tvTitle;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ListView_DownloadTask_Adapter adapter;

    private List<DownloadTask> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_detail);

        init();
        setData();
        setListener();
    }

    private void init() {
        iBack = findViewById(R.id.ibBack);
        tvTitle = findViewById(R.id.tvTitle);

        refreshLayout = findViewById(R.id.swipeLayoutDetail);
        listView = findViewById(R.id.listDownloadTask);
        refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);
    }

    private void setData() {
        tvTitle.setText("我的下载");

        taskList = DownloadHelper.getAllTask();
        adapter = new ListView_DownloadTask_Adapter(this, taskList, this);
        listView.setAdapter(adapter);
    }

    private void setListener() {
        iBack.setOnClickListener(v -> {
            back();
        });

        refreshLayout.setOnRefreshListener(() -> {
            taskList.clear();
            List<DownloadTask> tasks = DownloadHelper.getAllTask();
            taskList.addAll(tasks);
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        });
    }


    @Override
    public void onItemClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()){
            case R.id.tvTaskOpen:
                openFile(taskList.get(position).progress.filePath);
                break;
            case R.id.tvTaskDel:
                removeTask(taskList.get(position));
                taskList.remove(position);
                adapter.notifyDataSetChanged();
                break;
            case R.id.tvTaskState:
                 controlDownload(taskList.get(position));
                 break;
        }
    }

    private void controlDownload(DownloadTask task) {
        Progress progress = task.progress;
        switch (progress.status){
            case  Progress.WAITING:
            case  Progress.FINISH:
                break;
            case  Progress.LOADING:
                task.pause();
                adapter.notifyDataSetChanged();
                break;
            case  Progress.PAUSE:
                task.start();
                adapter.notifyDataSetChanged();
                break;
            case  Progress.ERROR:
                task.restart();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void removeTask(DownloadTask task) {
        if(task.progress.status==Progress.FINISH){
            task.remove();
;        }else{
            task.remove(true);//同时删除未下完文件
        }
    }

    //无法打开置于内部位置文件
    private void openFile(String filePath) {
        Log.i(TAG, "openImageFile: " + filePath);
        Uri uri = FileProvider.getUriForFile(this, "com.nepalese.virgovideoplayer.provider", new File(filePath));
        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//必须
        intent.setDataAndType(uri, PathUtil.getIntentType(filePath));
        startActivity(intent);
    }
}