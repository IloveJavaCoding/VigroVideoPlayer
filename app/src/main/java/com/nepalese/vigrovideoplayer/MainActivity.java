package com.nepalese.vigrovideoplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.nepalese.vigrovideoplayer.data.Constants;
import com.nepalese.vigrovideoplayer.presentation.ui.HomeActivity;
import com.nepalese.vigrovideoplayer.presentation.service.NetworkService;
import com.nepalese.virgosdk.Helper.GlideImageHelper;
import com.nepalese.virgosdk.Util.SystemUtil;
import com.nepalese.virgosdk.VirgoView.VirgoImageView;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private int time = 3;
    private TextView tvCountDown;
    private ImageView imgCover;

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String IMG_URL = "http://pic1.win4000.com/mobile/2020-09-25/5f6d85e308442.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLayout();
        if(!SystemUtil.checkPermission(this, NEEDED_PERMISSIONS)){
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        init();
    }

    private void setLayout() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void init() {
        context = getApplicationContext();

        tvCountDown = findViewById(R.id.tvCountDown);
        imgCover = findViewById(R.id.imgCover);
        imgCover.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context).load(IMG_URL).into(imgCover);

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
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    tvCountDown.setText(msg.arg1+ "秒");
                    break;
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
                SystemUtil.showToast(getApplicationContext(),"Permission denied!");
                finish();
            }
        }
    }
}