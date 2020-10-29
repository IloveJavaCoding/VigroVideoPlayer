package com.nepalese.vigrovideoplayer.presentation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nepalese.vigrovideoplayer.data.Constants;
import com.nepalese.vigrovideoplayer.data.DBHelper;
import com.nepalese.vigrovideoplayer.presentation.component.FloatView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * @author nepalese on 2020/10/29 08:51
 * @usage
 */
public class NetworkService extends Service {
    private static final String TAG = "NetworkService";

    private Context context;
    private FloatView floatView;//自定义提示框
    private VirgoHandler handler;
    private DBHelper dbHelper;

    public static Intent getIntent(Context context, String action, String extras) {
        Intent intent = new Intent();
        intent.setClass(context, NetworkService.class);
        intent.putExtra("action", action);
        intent.putExtra("data", extras);
        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new VirgoHandler(this);
        dbHelper = DBHelper.getInstance(context);

//        registerEventBus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent == null ? null : intent.getExtras();
        if (extras != null) {
            doCheck(extras.getString("action"), extras.getString("data"));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unRegisterEventBus();
    }

    //=================================================================================
    private void doCheck(String action, String data) {
        if (TextUtils.isEmpty(action)) return;
        if (action.equals(Constants.ACTION_START_HOME)) {
            startHome();
            return;
        }
    }

    private void startHome() {

    }

    //==================================================================================
//    private void registerEventBus() {
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//    }
//
//    private void unRegisterEventBus() {
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//    }

    private void post(Object object) {
        EventBus.getDefault().post(object);
    }




    //======================================================================================
    private static class VirgoHandler extends Handler{
        WeakReference<NetworkService> reference;

        public VirgoHandler(NetworkService networkService){
            reference = new WeakReference<>(networkService);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            NetworkService networkService = reference.get();
            if(networkService!=null){
                switch (msg.what){
//                    case :
//                        break;
                }
            }
        }
    }


}
