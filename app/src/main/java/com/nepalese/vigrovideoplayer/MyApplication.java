package com.nepalese.vigrovideoplayer;

import android.app.Application;
import android.content.Context;

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
    }
}
