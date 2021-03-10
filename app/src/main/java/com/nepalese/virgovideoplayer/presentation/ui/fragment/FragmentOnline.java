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

import com.nepalese.virgocomponent.component.VirgoFileSelectorDialog;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.data.bean.LiveSource;
import com.nepalese.virgovideoplayer.presentation.adapter.ListView_Online_Adapter;
import com.nepalese.virgovideoplayer.presentation.parsexml.ParseLive;
import com.nepalese.virgovideoplayer.presentation.ui.FullVideoPlayerActivity;
import com.nepalese.virgosdk.Util.ScreenUtil;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentOnline extends Fragment implements SwipeRefreshLayout.OnRefreshListener, VirgoFileSelectorDialog.SelectFileCallback {
    private static final String TAG = "FragmentOnline";

    private static final int MSG_UPDATE_LIST = 1;
    private static final int MSG_ADD_LIST = 2;

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

//        <Live name = "CCTV1（综合）" url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"/>
//	<Live name = "CCTV2（财经）" url = "http://ivi.bupt.edu.cn/hls/cctv2hd.m3u8"/>
//	<Live name = "CCTV3（综艺）" url = "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8"/>
//	<Live name = "CCTV4（中文国际）" url = "http://ivi.bupt.edu.cn/hls/cctv4hd.m3u8"/>
//	<Live name = "CCTV5（体育）" url = "http://112.17.40.140/PLTV/88888888/224/3221226554/index.m3u8"/>
//	<Live name = "CCTV5+" url = "http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8"/>
//	<Live name = "CCTV6（电影）" url = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8"/>
//	<Live name = "CCTV7（国防军事）" url = "http://ivi.bupt.edu.cn/hls/cctv7hd.m3u8"/>
//	<Live name = "CCTV8（电视剧）" url = "http://ivi.bupt.edu.cn/hls/cctv8hd.m3u8"/>
//	<Live name = "CCTV9（记录）" url = "http://ivi.bupt.edu.cn/hls/cctv9hd.m3u8"/>
//	<Live name = "CCTV10（科教）" url = "http://ivi.bupt.edu.cn/hls/cctv10hd.m3u8"/>
//	<!--Live name = "CCTV11（戏曲）" url = "http://ivi.bupt.edu.cn/hls/cctv11hd.m3u8"/-->
//	<Live name = "CCTV12（社会与法）" url = "http://ivi.bupt.edu.cn/hls/cctv12hd.m3u8"/>
//	<Live name = "CCTV13（新闻）" url = "http://183.207.249.36:80/PLTV/4/224/3221227387/index.m3u8"/>
//	<Live name = "CCTV14（少儿）" url = "http://ivi.bupt.edu.cn/hls/cctv14hd.m3u8"/>
//	<!--Live name = "CCTV15（音乐）" url = "http://ivi.bupt.edu.cn/hls/cctv15hd.m3u8"/-->
//	<Live name = "CCTV17（农业农村）" url = "http://ivi.bupt.edu.cn/hls/cctv17hd.m3u8"/>

        liveList = dbHelper.getAllLiveSource();
        if(liveList==null || liveList.size()<1){
            LiveSource liveSource = new LiveSource();
            liveSource.setName("CCTV1（综合）");
            liveSource.setUrl("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8");
            liveList.add(liveSource);
            dbHelper.saveLiveSource(liveSource);

            LiveSource liveSource2 = new LiveSource();
            liveSource2.setName("CCTV6（电影）");
            liveSource2.setUrl("http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8");
            liveList.add(liveSource2);
            dbHelper.saveLiveSource(liveSource2);

            LiveSource liveSource3 = new LiveSource();
            liveSource3.setName("CCTV5");
            liveSource3.setUrl("http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8");
            liveList.add(liveSource3);
            dbHelper.saveLiveSource(liveSource3);
            LiveSource liveSource4 = new LiveSource();
            liveSource4.setName("CCTV13");
            liveSource4.setUrl("http://183.207.249.36:80/PLTV/4/224/3221227387/index.m3u8");
            liveList.add(liveSource4);
            dbHelper.saveLiveSource(liveSource4);
        }

        adapter = new ListView_Online_Adapter(context, liveList);
        listView.setAdapter(adapter);

        dialog.setDialogHeight(ScreenUtil.getScreenHeight(context)/2);//设置弹框高度（显示屏高度一半）
//        dialog.setRootPath(context.getExternalFilesDir(null).getAbsolutePath());
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
//            showLoading();
//            startLiveAddTask();
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

    private void startLiveAddTask() {
        Runnable runnable = () -> {
//            addLiveSource(getResources().openRawResource(R.raw.LiveSource));
            handler.sendEmptyMessage(MSG_UPDATE_LIST);
        };
        new Thread(runnable).start();
    }

    private void addLiveSource(InputStream stream){
        if(stream!=null) {
            ParseLive parser = new ParseLive();
            List<LiveSource> list= parser.parse(stream, "utf-8");
            for (LiveSource source : list ){
                dbHelper.saveLiveSource(source);
            }
        }else {
            Log.e(TAG, "addLiveSource: 文件不存在！");
        }
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

    private void HideLoading() {
        layoutLoad.setVisibility(View.INVISIBLE);
    }

    private void showLoading() {
        layoutLoad.setVisibility(View.VISIBLE);
    }

    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_LIST:
                    updateList();
                    HideLoading();
                    break;
                case MSG_ADD_LIST:
                    showLoading();
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
