package com.nepalese.virgovideoplayer;

import android.app.Application;

import com.lzy.okgo.OkGo;

/**
 * @author nepalese on 2020/10/29 17:46
 * @usage
 */
public class MyApplication extends Application {
    private static MyApplication application;

    public MyApplication(){
        application = this;
    }

    public MyApplication getApplication(){
        if(application==null){
            synchronized (MyApplication.this){
                if(application==null){
                    application = new MyApplication();
                    application.onCreate();
                }
            }
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化OkGo
        OkGo.getInstance().init(this);
    }
}