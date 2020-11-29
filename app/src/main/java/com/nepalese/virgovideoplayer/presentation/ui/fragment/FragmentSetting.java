package com.nepalese.virgovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nepalese.virgosdk.Beans.M3U8;
import com.nepalese.virgosdk.Beans.M3U8Ts;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgosdk.Util.M3u8Util;
import com.nepalese.virgosdk.Util.SystemUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.presentation.helper.DownloadHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentSetting extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentSetting";

    private View rootView;
    private Context context;
    private DBHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        context = getContext();

        init();
        setData();

        return rootView;
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);

        rootView.findViewById(R.id.bClearAll).setOnClickListener(this);
        rootView.findViewById(R.id.bClearLocal).setOnClickListener(this);
        rootView.findViewById(R.id.bClearOnline).setOnClickListener(this);
        rootView.findViewById(R.id.bClearDownload).setOnClickListener(this);
        rootView.findViewById(R.id.bClearTask).setOnClickListener(this);
    }

    private void setData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bClearAll:
                dbHelper.clearAllTable();
                break;
            case R.id.bClearLocal:
                dbHelper.clearLocalVideo();
                break;
            case R.id.bClearOnline:
                dbHelper.clearLiveSource();
                break;
            case R.id.bClearDownload:
                dbHelper.clearDownloadItem();
                break;
            case R.id.bClearTask:
                DownloadHelper.clearAllTask();
                break;
        }
    }
}
