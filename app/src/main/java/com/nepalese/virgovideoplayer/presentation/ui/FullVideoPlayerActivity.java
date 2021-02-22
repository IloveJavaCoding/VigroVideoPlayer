package com.nepalese.virgovideoplayer.presentation.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.nepalese.virgosdk.VirgoView.VideoView.VirgoVideoViewSurface;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.presentation.bean.BaseActivity;

import java.io.File;

public class FullVideoPlayerActivity extends BaseActivity {
    private static final String TAG = "FullVideoPlayerActivity";

    private static final int MSG_HIDE_CONTROLLER = 1;
    private static final int MSG_SHOW_CONTROLLER = 2;
    private static final int HIDE_AFTER = 2345;

    private VirgoVideoViewSurface videoPlayer;
    private ImageButton ibControl;
    private RelativeLayout layout;

    private String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setContentView(R.layout.activity_full_video_player);

        getData();
        init();
        setData();
        setListener();
        startTask();
    }

    private void setLayout() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏StatusBar
    }

    private void getData() {
        videoPath = getIntent().getExtras().getString("path");
    }

    private void init() {
        layout = findViewById(R.id.fullVideoLayout);
        videoPlayer = findViewById(R.id.videoPlayer);
        ibControl = findViewById(R.id.ibVideoControl);
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
        layout.setOnClickListener(v -> {
            if(videoPlayer.isPlaying()){
                handler.sendEmptyMessage(MSG_SHOW_CONTROLLER);
                startTask();
            }
        });

        ibControl.setOnClickListener(v -> {
            if(videoPlayer.isPlaying()){
                cancelTask();
                videoPlayer.pause();
                ibControl.setImageResource(R.mipmap.icon_pause_200);
            }else{
                videoPlayer.continuePlay();
                ibControl.setImageResource(R.mipmap.icon_play_200);
                startTask();
            }
        });
    }

    private void startTask(){
        cancelTask();
        handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, HIDE_AFTER);
    }

    private void cancelTask(){
        handler.removeMessages(MSG_HIDE_CONTROLLER);
    }

    private void showController(){
        ibControl.setVisibility(View.VISIBLE);
    }

    private void hideController(){
        ibControl.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.stopPlay();
    }

    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_HIDE_CONTROLLER:
                    hideController();
                    break;
                case MSG_SHOW_CONTROLLER:
                    showController();
                    break;
            }
        }
    };
}