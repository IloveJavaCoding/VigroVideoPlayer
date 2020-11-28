package com.nepalese.virgovideoplayer.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentDownload;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentLocal;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentOnline;
import com.nepalese.virgovideoplayer.presentation.ui.fragment.FragmentSetting;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RadioGroup radioGroup;

    private FragmentLocal fragmentLocal;
    private FragmentOnline fragmentOnline;
    private FragmentDownload fragmentDownload;
    private FragmentSetting fragmentSetting;
    private FragmentTransaction transaction;

    private int checkIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setContentView(R.layout.activity_home);

        init();
        setListener();
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
                    checkIndex = 1;
                    Log.i(TAG, "onCheckedChanged: local");
                    showLocalFragment();
                    break;
                case R.id.radio_online:
                    checkIndex = 2;
                    Log.i(TAG, "onCheckedChanged: online");
                    showOnlineFragment();
                    break;
                case R.id.radio_download:
                    checkIndex = 3;
                    Log.i(TAG, "onCheckedChanged: online");
                    showDownloadFragment();
                    break;
                case R.id.radio_setting:
                    checkIndex = 4;
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
        if(fragmentLocal==null){
            fragmentLocal = new FragmentLocal();
            transaction.add(R.id.fragment_container, fragmentLocal);
        }else{
            transaction.show(fragmentLocal);
        }
    }

    private void showOnlineFragment(){
        if(fragmentOnline==null){
            fragmentOnline = new FragmentOnline();
            transaction.add(R.id.fragment_container, fragmentOnline);
        }else{
            transaction.show(fragmentOnline);
        }
    }

    private void showDownloadFragment(){
        if(fragmentDownload==null){
            fragmentDownload = new FragmentDownload();
            transaction.add(R.id.fragment_container, fragmentDownload);
        }else{
            transaction.show(fragmentDownload);
        }
    }

    private void showSettingFragment(){
        if(fragmentSetting==null){
            fragmentSetting = new FragmentSetting();
            transaction.add(R.id.fragment_container, fragmentSetting);
        }else{
            transaction.show(fragmentSetting);
        }
    }

//    @Override
//    protected void onResume() {
//        Log.i(TAG, "onResume: " + checkIndex);
//        super.onResume();
//        switch (checkIndex){
//            case 0:
//                break;
//            case 1:
//                radioGroup.check(R.id.radio_local);
//                break;
//            case 2:
//                radioGroup.check(R.id.radio_online);
//                break;
//            case 3:
//                radioGroup.check(R.id.radio_download);
//                break;
//            case 4:
//                radioGroup.check(R.id.radio_setting);
//                break;
//        }
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i(TAG, "onPause: hide all");
//        hideAllFragment(transaction);
//    }
}