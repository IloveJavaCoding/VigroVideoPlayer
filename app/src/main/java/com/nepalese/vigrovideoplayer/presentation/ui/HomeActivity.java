package com.nepalese.vigrovideoplayer.presentation.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.presentation.component.FileSelectorView;
import com.nepalese.vigrovideoplayer.presentation.component.FloatView;
import com.nepalese.vigrovideoplayer.presentation.ui.fragment.FragmentLocal;
import com.nepalese.vigrovideoplayer.presentation.ui.fragment.FragmentOnline;
import com.nepalese.vigrovideoplayer.presentation.ui.fragment.FragmentSetting;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RadioGroup radioGroup;

    private FragmentLocal fragmentLocal;
    private FragmentOnline fragmentOnline;
    private FragmentSetting fragmentSetting;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        setData();
        setListener();
    }

    private void init() {
        radioGroup = findViewById(R.id.radioGroup);
    }

    private void setData() {

    }

    private void setListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
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
                    case R.id.radio_setting:
                        Log.i(TAG, "onCheckedChanged: setting");
                        showSettingFragment();
                        break;
                }
                transaction.commit();
            }
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

    private void showSettingFragment(){
        if(fragmentSetting==null){
            fragmentSetting = new FragmentSetting();
            transaction.add(R.id.fragment_container, fragmentSetting);
        }else{
            transaction.show(fragmentSetting);
        }
    }
}