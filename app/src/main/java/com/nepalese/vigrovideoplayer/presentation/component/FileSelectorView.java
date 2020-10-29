package com.nepalese.vigrovideoplayer.presentation.component;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgosdk.Util.SystemUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author nepalese on 2020/10/29 10:09
 * @usage
 */
public class FileSelectorView extends LinearLayout implements ListView_FileSelector_Adapter.FileInterListener{
    private static final String TAG = "FileSelectorView";
    private static final int FLAG_DIR = 1;
    private static final int FLAG_FILE = 2;

    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private Context mCtx;

    private FileSelectorView self;
    private TextView tvCurPath, tvConfirm, tvResult;
    private LinearLayout layoutRoot, layoutLast;
    private ListView listView;
    private ListView_FileSelector_Adapter adapter;

    private String rootPath;
    private String curPath;
    private List<File> files;
    private List<Integer> index;

    private int flag = 0;

    public FileSelectorView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mCtx = context;
        self = this;
        wm = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        initWindow();
        setData();
        setListener();
        setOrientation(VERTICAL);
    }

    /**
     * 初始化windowManager
     */
    private void initWindow() {
        wmParams = getParams();
        wmParams.gravity = Gravity.CENTER;
        wmParams.x = 50;
        wmParams.y = 50;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mCtx);
        View view = mLayoutInflater.inflate(R.layout.layout_file_selector, null);

        tvCurPath = view.findViewById(R.id.tvCurPath);
        tvConfirm = view.findViewById(R.id.tvConfirmChoose);
        tvResult = view.findViewById(R.id.tvResult);

        layoutRoot = view.findViewById(R.id.layoutToRoot);
        layoutLast = view.findViewById(R.id.layoutToLast);

        listView = view.findViewById(R.id.listViewFile);
        index = new ArrayList<>();

        self.addView(view);
    }

    private void setData() {
        ///storage/emulated/0
        rootPath = "/storage/emulated/0";//FileUtil.getRootPath();
        curPath = rootPath;
        tvCurPath.setText(curPath);
        List<File> temp = Arrays.asList(new File(curPath).listFiles());
        files =  new ArrayList(temp);

        adapter = new ListView_FileSelector_Adapter(mCtx, files, this);//指向的是最开始的list
        listView.setAdapter(adapter);
    }

    /**
     * 对windowManager进行设置
     * @return
     */
    private WindowManager.LayoutParams getParams() {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return wmParams;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void destroy(){
        if (self.getParent() != null) {
            wm.removeView(self);
        }
    }

    public void show() {
        if (self.getParent() == null) {
            wm.addView(self, wmParams);
        } else {
            wm.updateViewLayout(self, wmParams);
        }
    }

    public void updateContent(String cont) {
        if (self.getParent() == null) {
            wm.addView(self, wmParams);
        }
        //显示内容
    }
    @Override
    public void itemClick(View v, boolean isChecked) {
        Integer position = (Integer) v.getTag();
        switch (v.getId()){
            case R.id.cbChoose:
                if(isChecked){
                    SystemUtil.showToast(mCtx, "choose " + (position+1));
                    index.add(position);
                }else{
                    index.remove(position);
                }
                break;
        }
    }

    private void resetData(String path){
        curPath = path;
        tvCurPath.setText(curPath);
        files.clear();
        index.clear();
        File[] fs = new File(curPath).listFiles();
        Arrays.sort(fs);
        List<File> temp = Arrays.asList(fs);
        for(File f : temp){
            files.add(f);
        }
        adapter.notifyDataSetChanged();
    }

    private void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("click", String.valueOf(position+1));
                //judge file/dir
                if(files.get(position).isFile()){
                    //
                }else{
                    resetData(files.get(position).getPath());
                }
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (flag){
                    case 0://test
                        if(index.size()>0){
                            StringBuilder builder = new StringBuilder();
                            for(int i=0; i<index.size(); i++){
                                builder.append(files.get(index.get(i)).getPath()+"\n");
                            }
                            tvResult.setText(builder.toString());
                        }else{
                            tvResult.setText("未选择任何目标！");
                        }
                        break;
                    case FLAG_DIR://return dirs
                        List<String> temp = new ArrayList<>();
                        if(index.size()>0){
                            for(int i=0; i<index.size(); i++){
                                if(files.get(index.get(i)).isDirectory()){
                                    temp.add(files.get(index.get(i)).getPath());
                                }
                            }
                        }else{
                            //
                        }
                        break;
                    case FLAG_FILE://return files
                        List<String> temp2 = new ArrayList<>();
                        if(index.size()>0){
                            for(int i=0; i<index.size(); i++){
                                if(files.get(index.get(i)).isFile()){
                                    temp2.add(files.get(index.get(i)).getPath());
                                }
                            }
                        }else{
                            //
                        }
                        break;
                }
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        self.destroy();
                    }
                }.start();
            }
        });

        layoutLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //judge curPath is root or not
                if (curPath.equals(rootPath)) {
                    //do nothing
                    SystemUtil.showToast(mCtx, "已是根目录");
                }else{
                    //back to last layer
                    resetData(curPath.substring(0, curPath.lastIndexOf("/")));
                }
            }
        });

        layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curPath.equals(rootPath)){
                    //do nothing
                    SystemUtil.showToast(mCtx, "已是根目录");
                }else{
                    //back to root
                    resetData(rootPath);
                }
            }
        });
    }
}
