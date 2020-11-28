package com.nepalese.virgovideoplayer;

import android.app.Application;
import android.content.Context;

import com.lzy.okgo.OkGo;

/**
 * @author nepalese on 2020/10/29 17:46
 * @usage
 */
public class MyApplication extends Application {
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //初始化OkGo
        OkGo.getInstance().init(this);
    }
}
