package com.nepalese.virgovideoplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.nepalese.virgosdk.Base.BaseActivity;
import com.nepalese.virgosdk.Util.MathUtil;
import com.nepalese.virgosdk.Util.SystemUtil;
import com.nepalese.virgovideoplayer.data.Constants;
import com.nepalese.virgovideoplayer.presentation.service.NetworkService;

public class MainActivity extends BaseActivity {
    private TextView tvCountDown;
    private ImageView imgCover;
    private int time = 3;

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String IMG_URL1 = "http://pic1.win4000.com/mobile/2020-09-25/5f6d85e308442.jpg";
    private static final String IMG_URL2 = "http://pic1.win4000.com/mobile/2020-11-27/5fc0be51a0ede.jpg";
    private static final String IMG_URL3 = "http://pic1.win4000.com/mobile/2020-11-25/5fbe1060ee877.jpg";
    private static final String IMG_URL4 = "http://pic1.win4000.com/mobile/2020-11-26/5fbf75203547d.jpg";
    private static final String IMG_URL5 = "http://pic1.win4000.com/mobile/2020-11-26/5fbf752241d13.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setContentView(R.layout.activity_main);

        if(!SystemUtil.checkPermission(this, NEEDED_PERMISSIONS)){
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        init();
    }

    private void setLayout() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    private void init() {
        Context context = getApplicationContext();

        tvCountDown = findViewById(R.id.tvCountDown);
        imgCover = findViewById(R.id.imgCover);
        imgCover.setScaleType(ImageView.ScaleType.FIT_XY);

        switch (MathUtil.getRandomNumInt(1,5)){
            case 1:
                Glide.with(context).load(IMG_URL1).into(imgCover);
                break;
            case 2:
                Glide.with(context).load(IMG_URL2).into(imgCover);
                break;
            case 3:
                Glide.with(context).load(IMG_URL3).into(imgCover);
                break;
            case 4:
                Glide.with(context).load(IMG_URL4).into(imgCover);
                break;
            case 5:
                Glide.with(context).load(IMG_URL5).into(imgCover);
                break;
        }

        //开启后台服务 进入主界面
        startService(NetworkService.getIntent(context, Constants.ACTION_START_HOME, null));
        countDown();
    }

    private void countDown() {
        handler.post(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(time>0){
                Message message = Message.obtain();
                message.what = 0;
                message.arg1 = time;
                handler.sendMessage(message);

                time--;
                handler.postDelayed(runnable, 1000);
            }else{
                finish();
            }
        }
    };

    private Handler handler = new Handler(Looper.myLooper()){
        @SuppressLint("DefaultLocale")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                tvCountDown.setText(String.format("%d秒", msg.arg1));
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
        }

        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                //get all requested permissions
                init();
            } else {
                showToast("Permission denied!");
                finish();
            }
        }
    }
}