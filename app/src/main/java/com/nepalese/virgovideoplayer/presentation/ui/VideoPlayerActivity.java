package com.nepalese.virgovideoplayer.presentation.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.data.bean.Video;
import com.nepalese.virgovideoplayer.presentation.component.LocalVideoPlayer;

import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VideoPlayerActivity";

    private Context context;
    private DBHelper dbHelper;

    private LocalVideoPlayer videoPlayer;
    private RelativeLayout layoutAll, layoutController, layoutTitle;
    private ImageButton iBack;
    private TextView tvTitle;

    private List<Video> vList;
    private int curIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

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
        curIndex = getIntent().getExtras().getInt("index");
        Log.i(TAG, "getData: " + curIndex);
    }

    private void init() {
        context = getApplicationContext();
        dbHelper = DBHelper.getInstance(context);

        layoutAll = findViewById( R.id.layoutAll);
        layoutController = findViewById(R.id.layoutController);
        layoutTitle = findViewById(R.id.video_title);

        videoPlayer = findViewById(R.id.videoPlayer);
        iBack = findViewById(R.id.ibBack);
        tvTitle = findViewById(R.id.tvTitle);

//        fullShow();
    }

    private void fullShow(){
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutAll.setLayoutParams(layoutParams);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setData() {
        vList = dbHelper.getAllVideo();
        videoPlayer.setUrl(vList, curIndex).play();
    }

    private void setListener() {
        iBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibBack:
                finish();
                break;
        }
    }
}