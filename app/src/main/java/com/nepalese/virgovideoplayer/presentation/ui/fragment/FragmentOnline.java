package com.nepalese.virgovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.data.bean.LiveSource;
import com.nepalese.virgovideoplayer.presentation.adapter.ListView_Online_Adapter;
import com.nepalese.virgovideoplayer.presentation.component.VirgoFileSelectorDialog;
import com.nepalese.virgovideoplayer.presentation.parsexml.ParseLive;
import com.nepalese.virgovideoplayer.presentation.ui.FullVideoPlayerActivity;
import com.nepalese.virgosdk.Util.ScreenUtil;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentOnline extends Fragment implements SwipeRefreshLayout.OnRefreshListener, VirgoFileSelectorDialog.SelectFileCallback {
    private static final String TAG = "FragmentOnline";

    private static final int MSG_UPDATE_LIST = 0;
    private static final int MSG_ADD_LIST = 1;

    private View rootView;
    private Context context;
    private DBHelper dbHelper;
    private VirgoFileSelectorDialog dialog;

    private ListView listView;
    private ListView_Online_Adapter adapter;
    private ImageButton ibAdd;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout layoutLoad;

    private List<LiveSource> liveList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_online, container, false);
        context = getContext();

        init();
        setData();
        setListener();

        return rootView;
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);
        dialog = new VirgoFileSelectorDialog(context);

        ibAdd = rootView.findViewById(R.id.ibOnlineAdd);
        refreshLayout = rootView.findViewById(R.id.swipeLayout);
        listView = rootView.findViewById(R.id.listOnline);
        layoutLoad = rootView.findViewById(R.id.layoutLoad);

        refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);
    }

    private void setData() {
        //(url=http://183.207.249.36:80/PLTV/4/224/3221227387/index.m3u8) : CCTV13
        //url=http://112.17.40.140/PLTV/88888888/224/3221226554/index.m3u8 : CCTV5

        liveList = dbHelper.getAllLiveSource();
//        if(liveList==null || liveList.size()<1){
//            LiveSource liveSource = new LiveSource();
//            liveSource.setName("CCTV5");
//            liveSource.setUrl("http://112.17.40.140/PLTV/88888888/224/3221226554/index.m3u8");
//            liveList.add(liveSource);
//            dbHelper.saveLiveSource(liveSource);
//            LiveSource liveSource2 = new LiveSource();
//            liveSource2.setName("CCTV13");
//            liveSource2.setUrl("http://183.207.249.36:80/PLTV/4/224/3221227387/index.m3u8");
//            dbHelper.saveLiveSource(liveSource2);
//        }

        adapter = new ListView_Online_Adapter(context, liveList);
        listView.setAdapter(adapter);

        dialog.setDialogHeight(ScreenUtil.getScreenHeight(context)/2);//设置弹框高度（显示屏高度一半）
        dialog.setFlag(VirgoFileSelectorDialog.FLAG_FILE);//设置要选择的是文件还是文件夹
        dialog.setFileType(VirgoFileSelectorDialog.TYPE_TEXT);
        dialog.setCallback(this);//设置回调，必选，若要返回值
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(this);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "onItemClick: ");
            Intent intent = new Intent(context, FullVideoPlayerActivity.class);
            intent.putExtra("path", liveList.get(position).getUrl());
            startActivity(intent);
        });

        ibAdd.setOnClickListener(v -> {
            dialog.show();
        });
    }

    private void startLiveAddTask(List<File> list) {
        Runnable runnable = () -> {
            for (File f: list){
                addLiveSource(f.getAbsolutePath());
            }
            handler.sendEmptyMessage(MSG_UPDATE_LIST);
        };
         new Thread(runnable).start();
    }

    private void addLiveSource(String path){
        File file = new File(path);
        if(file.exists()) {
            ParseLive parser = new ParseLive();
            List<LiveSource> list= parser.parse(file, "utf-8");
            for (LiveSource source : list ){
                dbHelper.saveLiveSource(source);
            }
        }else {
            Log.e(TAG, "addLiveSource: 文件不存在！");
        }
    }

    private void updateList() {
        liveList.clear();
        List<LiveSource> temp = dbHelper.getAllLiveSource();
        liveList.addAll(temp);
        adapter.notifyDataSetChanged();
    }

    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_LIST:
                    updateList();
                    break;
                case MSG_ADD_LIST:
                    startLiveAddTask((List<File>)msg.obj);
                    break;
            }
        }
    };


    @Override
    public void onRefresh() {
        updateList();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onResult(List<File> list) {
        Log.i(TAG, "onResult: ");
        if(list!=null && list.size()>0){
            //在后台进行加载，扫描
            Message message = Message.obtain();
            message.what = MSG_ADD_LIST;
            message.obj = list;
            handler.sendMessage(message);
        }
    }
}
