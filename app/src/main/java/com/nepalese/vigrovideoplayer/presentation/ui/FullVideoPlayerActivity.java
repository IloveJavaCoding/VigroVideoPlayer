package com.nepalese.vigrovideoplayer.presentation.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.presentation.bean.BaseActivity;
import com.nepalese.virgosdk.VirgoView.VideoView.VirgoVideoViewSurface;

import java.io.File;

public class FullVideoPlayerActivity extends BaseActivity {
    private static final String TAG = "FullVideoPlayerActivity";

    private Context context;

    private VirgoVideoViewSurface videoPlayer;
    private String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video_player);

        setLayout();
        getData();
        init();
        setData();
        setListener();
    }

    private void setLayout() {
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    private void getData() {
        videoPath = getIntent().getExtras().getString("path");
    }

    private void init() {
        videoPlayer = findViewById(R.id.videoPlayer);
    }

    private void setData() {
        File file = new File(videoPath);
        if(file.exists()){
            videoPlayer.setVideoUri(Uri.fromFile(file));
        }else{
            videoPlayer.setVideoUri(Uri.parse(videoPath));
        }
        videoPlayer.start();
    }

    private void setListener() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.stopPlay();
    }
}