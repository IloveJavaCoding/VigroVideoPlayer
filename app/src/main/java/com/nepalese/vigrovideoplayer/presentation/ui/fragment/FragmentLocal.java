package com.nepalese.vigrovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.data.DBHelper;
import com.nepalese.vigrovideoplayer.data.bean.Video;
import com.nepalese.vigrovideoplayer.presentation.adapter.GridView_Local_Adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2020/10/29 12:00
 * @usage
 */
public class FragmentLocal extends Fragment {
    private static final String TAG = "FragmentLocal";
    private Context context;
    private DBHelper dbHelper;

    private View rootView;
    private ImageButton ibList;
    private TextView tvNote;
    private GridView gridView;
    private GridView_Local_Adapter adapter;

    private List<Video> videoList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_local, container, false);
        context = getContext();

        init();
        setData();
        setListener();

        return rootView;
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);

        ibList = rootView.findViewById(R.id.ibList);
        tvNote = rootView.findViewById(R.id.tvNote);
        gridView = rootView.findViewById(R.id.gridViewLocal);
    }

    private void setData() {
        videoList = dbHelper.getAllVideo();
        adapter = new GridView_Local_Adapter(context, videoList);

        if(videoList==null || videoList.size()==0){
            tvNote.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {

    }

    private void hideNote(){
        tvNote.setVisibility(View.INVISIBLE);
    }
}
