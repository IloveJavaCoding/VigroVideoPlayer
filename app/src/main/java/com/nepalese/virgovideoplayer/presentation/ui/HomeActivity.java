package com.nepalese.virgovideoplayer.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentDownload;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentLocal;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentOnline;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentSetting;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RadioGroup radioGroup;

    private FragmentLocal fragmentLocal = null;
    private FragmentOnline fragmentOnline = null;
    private FragmentDownload fragmentDownload = null;
    private FragmentSetting fragmentSetting = null;
    private FragmentTransaction transaction;

    private static final String FileName = "VirgoVideo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setContentView(R.layout.activity_home);

        makeDir();
        init();
        setListener();
    }

    private void makeDir(){
        String path = FileUtil.getRootPath() + "/Download/" + FileName;
        File file = new File(path);
        if(!file.exists()){
            Log.i(TAG, "makeDir: ");
            file.mkdir();
        }
    }

    private void init() {
        radioGroup = findViewById(R.id.radioGroup);
    }

    private void setLayout() {
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    private void setListener() {
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            transaction = getSupportFragmentManager().beginTransaction();
            hideAllFragment(transaction);
            switch (i){
                case R.id.radio_local:
                    Log.i(TAG, "onCheckedChanged: local");
                    showLocalFragment();
                    break;
                case R.id.radio_online:
                    Log.i(TAG, "onCheckedChanged: online");
                    showOnlineFragment();
                    break;
                case R.id.radio_download:
                    Log.i(TAG, "onCheckedChanged: online");
                    showDownloadFragment();
                    break;
                case R.id.radio_setting:
                    Log.i(TAG, "onCheckedChanged: setting");
                    showSettingFragment();
                    break;
            }
            transaction.commit();
        });

        //默认选择
        radioGroup.check(R.id.radio_online);
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if(fragmentLocal!=null){
            transaction.hide(fragmentLocal);
            //transaction.remove(fragment_mine);
        }
        if(fragmentOnline!=null){
            transaction.hide(fragmentOnline);
        }

        if(fragmentDownload!=null){
            transaction.hide(fragmentDownload);
        }

        if(fragmentSetting!=null){
            transaction.hide(fragmentSetting);
        }
    }

    private void showLocalFragment(){
        if(fragmentLocal!=null){
            transaction.show(fragmentLocal);
        }else{
            fragmentLocal = new FragmentLocal();
            transaction.add(R.id.fragment_container, fragmentLocal);
        }
    }

    private void showOnlineFragment(){
        if(fragmentOnline!=null){
            transaction.show(fragmentOnline);
        }else{
            fragmentOnline = new FragmentOnline();
            transaction.add(R.id.fragment_container, fragmentOnline);
        }
    }

    private void showDownloadFragment(){
        if(fragmentDownload!=null){
            transaction.show(fragmentDownload);
        }else{
            fragmentDownload = new FragmentDownload();
            transaction.add(R.id.fragment_container, fragmentDownload);
        }
    }

    private void showSettingFragment(){
        if(fragmentSetting!=null){
            transaction.show(fragmentSetting);
        }else{
            fragmentSetting = new FragmentSetting();
            transaction.add(R.id.fragment_container, fragmentSetting);
        }
    }
}