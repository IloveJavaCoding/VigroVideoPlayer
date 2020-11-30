package com.nepalese.virgovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgosdk.Util.ScreenUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.Constants;
import com.nepalese.virgovideoplayer.data.DBHelper;
import com.nepalese.virgovideoplayer.data.bean.Video;
import com.nepalese.virgovideoplayer.presentation.adapter.ListView_VideoList_Adapter;
import com.nepalese.virgovideoplayer.presentation.component.VirgoFileSelectorDialog;
import com.nepalese.virgovideoplayer.presentation.event.FinishScanEvent;
import com.nepalese.virgovideoplayer.presentation.event.StartScanVideoEvent;
import com.nepalese.virgovideoplayer.presentation.ui.VideoPlayerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import scut.carson_ho.kawaii_loadingview.Kawaii_LoadingView;

/**
 * @author nepalese on 2020/10/29 12:00
 * @usage
 */
public class FragmentLocal extends Fragment implements VirgoFileSelectorDialog.SelectFileCallback {
    private static final String TAG = "FragmentLocal";
    private static final int MSG_FLASH_LIST = 1;

    private Context context;
    private DBHelper dbHelper;

    private View rootView;
    private ImageButton ibList;
    private TextView tvNote;
    private ListView listView;
    private ListView_VideoList_Adapter adapter;
    private VirgoFileSelectorDialog dialog;
    private Kawaii_LoadingView loadingView;

    private List<Video> videoList;
    private String rootPath, thumbPath;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_local, container, false);
        context = getContext();

        registerEventBus();
        createFile();
        init();
        setData();
        setListener();

        return rootView;
    }

    private void createFile() {
        rootPath = FileUtil.getAppRootPth(context);
        thumbPath = rootPath + File.separator + Constants.DIR_THUMB_NAIL;
        File file = new File(thumbPath);
        if(!file.exists()){
            if(!file.mkdir()){
                return;
            }
        }
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);
        dialog = new VirgoFileSelectorDialog(context);

        ibList = rootView.findViewById(R.id.ibList);
        tvNote = rootView.findViewById(R.id.tvNote);
        listView = rootView.findViewById(R.id.listViewVideoLocal);
        loadingView = rootView.findViewById(R.id.loadingView);
    }

    private void setData() {
        videoList = dbHelper.getAllVideo();
        if(videoList==null || videoList.size()==0){
           showNote();
        }

        adapter = new ListView_VideoList_Adapter(context, videoList);
        listView.setAdapter(adapter);

        dialog.setDialogHeight(ScreenUtil.getScreenHeight(context)/2);//设置弹框高度（显示屏高度一半）
        dialog.setFlag(VirgoFileSelectorDialog.FLAG_DIR);//设置要选择的是文件还是文件夹
        dialog.setCallback(this);//设置回调，必选，若要返回值
    }

    private void setListener() {
        ibList.setOnClickListener(v->{
            dialog.show();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "onItemClick: " + videoList.get(position).getName());
            Intent intent = new Intent(context.getApplicationContext(), VideoPlayerActivity.class);
            intent.putExtra("index", position);
            startActivity(intent);
        });
    }

    private void flashData() {
        videoList.clear();
        List<Video> temp = dbHelper.getAllVideo();
        if(temp!=null && temp.size()>0){
            hideNote();
            videoList.addAll(temp);
        }else{
            showNote();
        }

        adapter.notifyDataSetChanged();
    }

    private void clearThumb() {
        File file = new File(thumbPath);
        File[] f = file.listFiles();
        if(f!=null){
            for(File f1: f){
                f1.delete();
            }
        }
    }

    private void hideNote(){
        tvNote.setVisibility(View.INVISIBLE);
    }

    private void showNote(){
        tvNote.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        Log.i(TAG, "hideLoadingView: ");
        loadingView.stopMoving();
        loadingView.setVisibility(View.INVISIBLE);
    }

    private void showLoadingView() {
        Log.i(TAG, "showLoadingView: ");
        loadingView.setVisibility(View.VISIBLE);
        loadingView.startMoving();
    }

    @Override
    public void onResult(List<File> list) {
        Log.i(TAG, "onResult: ");
        if(list!=null && list.size()>0){
            //在后台进行加载，扫描
            Message message = Message.obtain();
            message.what = MSG_FLASH_LIST;
            message.obj = list;
            handler.sendMessage(message);
            postEvent(new StartScanVideoEvent(list));
        }
    }

    @Override
    public void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }

    //==================================================================================
    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void postEvent(Object object) {
        EventBus.getDefault().post(object);
    }

    @Subscribe
    public void onMainThread(FinishScanEvent event){
        Log.i(TAG, "onMainThread: FinishScanEvent");
        hideLoadingView();
        flashData();
    }

    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_FLASH_LIST) {
                showLoadingView();
                clearThumb();
                dbHelper.clearLocalVideo();
            }
        }
    };
}
