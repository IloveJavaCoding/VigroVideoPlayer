package com.nepalese.virgovideoplayer.presentation.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lzy.okserver.download.DownloadTask;
import com.nepalese.virgosdk.Base.BaseActivity;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.presentation.adapter.ListView_DownloadTask_Adapter;
import com.nepalese.virgovideoplayer.presentation.helper.DownloadHelper;

import java.util.List;

public class DownloadDetailActivity extends BaseActivity {
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
        adapter = new ListView_DownloadTask_Adapter(this, taskList);
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
}