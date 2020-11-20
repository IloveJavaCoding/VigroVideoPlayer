package com.nepalese.vigrovideoplayer.presentation.ui.fragment;

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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.data.Constants;
import com.nepalese.vigrovideoplayer.data.DBHelper;
import com.nepalese.vigrovideoplayer.data.bean.Video;
import com.nepalese.vigrovideoplayer.presentation.adapter.GridView_Local_Adapter;
import com.nepalese.vigrovideoplayer.presentation.component.VirgoFileSelectorDialog;
import com.nepalese.vigrovideoplayer.presentation.event.FinishScanEvent;
import com.nepalese.vigrovideoplayer.presentation.event.StartScanVideoEvent;
import com.nepalese.vigrovideoplayer.presentation.ui.VideoPlayerActivity;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgosdk.Util.ScreenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/10/29 12:00
 * @usage
 */
public class FragmentLocal extends Fragment implements VirgoFileSelectorDialog.SelectFileCallback {
    private static final String TAG = "FragmentLocal";
    private static final int MSG_FLASH_LIST = 0;

    private Context context;
    private DBHelper dbHelper;

    private View rootView;
    private ImageButton ibList;
    private TextView tvNote;
    private GridView gridView;
    private GridView_Local_Adapter adapter;
    private VirgoFileSelectorDialog dialog;
    private LinearLayout layoutLoad;

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
            file.mkdir();
        }
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);
        dialog = new VirgoFileSelectorDialog(context, R.style.File_Dialog);

        ibList = rootView.findViewById(R.id.ibList);
        tvNote = rootView.findViewById(R.id.tvNote);
        gridView = rootView.findViewById(R.id.gridViewLocal);
        layoutLoad = rootView.findViewById(R.id.layoutLoad);
    }

    private void setData() {
        videoList = dbHelper.getAllVideo();
        if(videoList==null || videoList.size()==0){
            tvNote.setVisibility(View.VISIBLE);
        }

        adapter = new GridView_Local_Adapter(context, videoList);
        gridView.setAdapter(adapter);

        dialog.setDialogHeight(ScreenUtil.getScreenHeight(context)/2);//设置弹框高度（显示屏高度一半）
        dialog.setFlag(VirgoFileSelectorDialog.FLAG_DIR);//设置要选择的是文件还是文件夹
        dialog.setCallback(this);//设置回调，必选，若要返回值
    }

    private void setListener() {
        ibList.setOnClickListener(v->{
            dialog.show();
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " + videoList.get(position).getName());
                Intent intent = new Intent(context.getApplicationContext(), VideoPlayerActivity.class);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });
    }

    private void flashData() {
        List<Video> temp = dbHelper.getAllVideo();
        if(temp!=null && temp.size()>0){
            hideNote();
            videoList.addAll(temp);
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

    private void hideLoadingView() {
        layoutLoad.setVisibility(View.INVISIBLE);
    }

    private void showLoadingView() {
        layoutLoad.setVisibility(View.VISIBLE);
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
            switch (msg.what){
                case MSG_FLASH_LIST:
                    showLoadingView();
                    clearThumb();
                    postEvent(new StartScanVideoEvent((List<File>)msg.obj));
                    break;
            }
        }
    };
}
