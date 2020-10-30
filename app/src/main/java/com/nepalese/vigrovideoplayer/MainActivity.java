package com.nepalese.vigrovideoplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.nepalese.vigrovideoplayer.presentation.ui.HomeActivity;
import com.nepalese.vigrovideoplayer.presentation.service.NetworkService;
import com.nepalese.virgosdk.Helper.GlideImageHelper;
import com.nepalese.virgosdk.Util.SystemUtil;
import com.nepalese.virgosdk.VirgoView.VirgoImageView;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private final int time = 3;
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

        if(!SystemUtil.checkPermission(this, NEEDED_PERMISSIONS)){
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        init();
    }

    private void init() {
        context = getApplicationContext();

        tvCountDown = findViewById(R.id.tvCountDown);
        imgCover = findViewById(R.id.imgCover);
        new GlideImageHelper(1).displayImage(context,IMG_URL, imgCover);

        //开启后台服务 进入主界面
        startService(NetworkService.getIntent(context, null, null));
        countDown();
    }

    private void countDown(){
        new Thread(){
            int times = time;
            @Override
            public void run() {
                super.run();
                while (times>0){
                    Message message = Message.obtain();
                    message.what = 0;
                    message.arg1 = times;
                    handler.sendMessage(message);

                    times--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                startHome();
            }
        }.start();
    }

    private void startHome() {
//        new Handler(Looper.myLooper()).postDelayed(() -> {
//
//        },time*1000);
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
        finish();
    }

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

            } else {
                SystemUtil.showToast(getApplicationContext(),"Permission denied!");
                finish();
            }
        }
    }
}