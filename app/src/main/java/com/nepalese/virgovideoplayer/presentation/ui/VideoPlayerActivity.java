package com.nepalese.virgovideoplayer.presentation.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nepalese.virgosdk.Base.BaseEventActivity;
import com.nepalese.virgosdk.Util.ConvertUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.data.bean.Video;
import com.nepalese.virgovideoplayer.presentation.adapter.GridView_VideoList_Adapter;
import com.nepalese.virgovideoplayer.presentation.component.LocalVideoPlayer;
import com.nepalese.virgovideoplayer.presentation.event.LocalVideoPlayEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VideoPlayerActivity extends BaseEventActivity implements View.OnClickListener {
    private static final String TAG = "VideoPlayerActivity";

    private static final int MSG_HIDE_CONTROLLER = 1;
    private static final int MSG_SHOW_CONTROLLER = 2;
    private static final int MSG_CUR_TIME = 3;
    private static final int HIDE_AFTER = 3000;
    private static final int FLASH_TIME_INTERVAL = 500;

    private Context context;
    private DBHelper dbHelper;

    private LocalVideoPlayer videoPlayer;
    private RelativeLayout layoutAll, layoutTitle, layoutGridView;
    private LinearLayout layoutController;
    private ImageButton iBack, ibPlay, ibFull;
    private TextView tvTitle, tvCurTime, tvTotalTime;
    private SeekBar seekBar;

    private GridView gridView;
    private GridView_VideoList_Adapter adapter;

    private List<Video> vList;
    private int curIndex = 0;
    private boolean isFull = false;
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
        layoutTitle = findViewById(R.id.layoutTitle);

        videoPlayer = findViewById(R.id.videoPlayer);
        iBack = findViewById(R.id.ibBack);
        tvTitle = findViewById(R.id.tvTitle);

        //controller
        ibPlay = findViewById(R.id.ctrlIbPlay);
        ibFull = findViewById(R.id.ctrlIbFull);
        tvCurTime = findViewById(R.id.ctrlCurTime);
        tvTotalTime = findViewById(R.id.ctrlTotalTime);
        seekBar = findViewById(R.id.ctrlSeekBar);

        layoutGridView = findViewById(R.id.layoutGridView);
        gridView = findViewById(R.id.gridViewVideoList);
    }

    private void setData() {
        vList = dbHelper.getAllVideo();

        videoPlayer.setUrl(vList, curIndex).play();
        startFlashTask();

        adapter = new GridView_VideoList_Adapter(context, vList);
        gridView.setAdapter(adapter);
    }

    private void updateData(){
        Video video = vList.get(curIndex);
        tvTitle.setText(video.getName());
        tvTotalTime.setText(ConvertUtil.formatTime(video.getDuration()));
        seekBar.setMax((int) video.getDuration());
    }

    private void setListener() {
        iBack.setOnClickListener(this);
        ibPlay.setOnClickListener(this);
        ibFull.setOnClickListener(this);
        layoutAll.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoPlayer.seekTo(seekBar.getProgress());
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                videoPlayer.setCurrentIndex(position);
                videoPlayer.play();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layoutAll:
                if(videoPlayer.isPlaying()){
                    showController();
                }
                break;
            case R.id.ibBack:
                back();
                break;
            case R.id.ctrlIbPlay:
                if(videoPlayer.isPlaying()){
                    cancelHideTask();
                    videoPlayer.pause();
                    ibPlay.setImageResource(R.mipmap.icon_pause_32);
                }else{
                    videoPlayer.continuePlay();
                    ibPlay.setImageResource(R.mipmap.icon_play_32);
                    startHideTask();
                }
                break;
            case R.id.ctrlIbFull:
                if(!isFull){
                    isFull = true;
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else{
                    isFull = false;
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                Log.i(TAG, "onConfigurationChanged: portrait");
                showController();
                outOfFull();
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.i(TAG, "onConfigurationChanged: landscape");
                showController();
                fullMode();
                break;
            case Configuration.ORIENTATION_SQUARE:
            case Configuration.ORIENTATION_UNDEFINED:
                break;
        }
    }

    private void outOfFull() {
        layoutGridView.setVisibility(View.VISIBLE);
        setLayoutNormal();
        RelativeLayout.LayoutParams lp = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.video_height));
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutAll.setLayoutParams(lp);
    }

    private void fullMode(){
        layoutGridView.setVisibility(View.INVISIBLE);
        setLayoutFull();
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutAll.setLayoutParams(layoutParams);
    }

    private void setLayoutNormal() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setLayoutFull() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏StatusBar
    }

    private void startHideTask(){
        cancelHideTask();
        handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, HIDE_AFTER);
    }

    private void cancelHideTask(){
        handler.removeMessages(MSG_HIDE_CONTROLLER);
    }

    private void showController(){
        layoutTitle.setVisibility(View.VISIBLE);
        layoutController.setVisibility(View.VISIBLE);
        startHideTask();
    }

    private void hideController(){
        layoutTitle.setVisibility(View.INVISIBLE);
        layoutController.setVisibility(View.INVISIBLE);
    }

    private void startFlashTask(){
        Log.i(TAG, "startFlashTask: ");
        handler.post(flashCurTime);
    }

    private void stopFlashTask(){
        handler.removeCallbacks(flashCurTime);
    }

    private final Runnable flashCurTime = new Runnable() {
        @Override
        public void run() {
            if(videoPlayer.isPlaying()){
                Message msg = new Message();
                msg.what = MSG_CUR_TIME;
                msg.arg1 = videoPlayer.getCurrentPosition();
                handler.sendMessage(msg);
            }

            handler.postDelayed(flashCurTime, FLASH_TIME_INTERVAL);
        }
    };

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
                case MSG_CUR_TIME:
                    tvCurTime.setText(ConvertUtil.formatTime((int) msg.arg1));
                    seekBar.setProgress((int) msg.arg1);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.stopPlay();
        stopFlashTask();
    }

    @Subscribe
    public void onMainThread(LocalVideoPlayEvent event){
        handler.sendEmptyMessage(MSG_SHOW_CONTROLLER);
        curIndex = event.getCurIndex();
        updateData();
    }
}