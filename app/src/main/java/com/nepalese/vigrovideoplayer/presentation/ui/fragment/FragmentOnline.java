package com.nepalese.vigrovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.data.DBHelper;
import com.nepalese.vigrovideoplayer.data.bean.LiveSource;
import com.nepalese.vigrovideoplayer.presentation.adapter.ListView_Online_Adapter;
import com.nepalese.vigrovideoplayer.presentation.ui.FullVideoPlayerActivity;

import java.util.List;

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentOnline extends Fragment{
    private static final String TAG = "FragmentOnline";

    private View rootView;
    private Context context;
    private DBHelper dbHelper;

    private ListView listView;
    private ListView_Online_Adapter adapter;
    private LinearLayout layoutLoad;

    private List<LiveSource> liveList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_online, container, false);
        context = getContext();

        init();
        setData();
        setListener();

        return rootView;
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);
        listView = rootView.findViewById(R.id.listOnline);
        layoutLoad = rootView.findViewById(R.id.layoutLoad);
    }

    private void setData() {
        //(url=http://183.207.249.36:80/PLTV/4/224/3221227387/index.m3u8) : CCTV13
        //url=http://112.17.40.140/PLTV/88888888/224/3221226554/index.m3u8 : CCTV5
        liveList = dbHelper.getAllLiveSource();
        if(liveList==null || liveList.size()<1){
            LiveSource liveSource = new LiveSource();
            liveSource.setName("CCTV5");
            liveSource.setUrl("http://112.17.40.140/PLTV/88888888/224/3221226554/index.m3u8");
            liveList.add(liveSource);
            dbHelper.saveLiveSource(liveSource);
            LiveSource liveSource2 = new LiveSource();
            liveSource2.setName("CCTV13");
            liveSource2.setUrl("http://183.207.249.36:80/PLTV/4/224/3221227387/index.m3u8");
            liveList.add(liveSource);
            dbHelper.saveLiveSource(liveSource2);
        }

        adapter = new ListView_Online_Adapter(context, liveList);
        listView.setAdapter(adapter);
    }

    private void setListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "onItemClick: ");
            Intent intent = new Intent(context, FullVideoPlayerActivity.class);
            intent.putExtra("path", liveList.get(position).getUrl());
            startActivity(intent);
        });
    }
}
