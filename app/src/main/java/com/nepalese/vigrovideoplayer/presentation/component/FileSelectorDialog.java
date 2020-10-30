package com.nepalese.vigrovideoplayer.presentation.component;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.presentation.adapter.ListView_FileSelector_Adapter;
import com.nepalese.virgosdk.Util.SystemUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author nepalese on 2020/10/30 15:20
 * @usage
 */
public class FileSelectorDialog extends Dialog implements ListView_FileSelector_Adapter.FileInterListener {
    private static final String TAG = "FileSelectorDialog";

    private static final int FLAG_DIR = 0;
    private static final int FLAG_FILE = 1;

    //仅显示某种特定文件
    private static final String TYPE_ALL = "all";
    private static final String TYPE_IMAGE = "image";
    private static final String TYPE_TEXT = "txt";
    private static final String TYPE_VIDEO = "video";
    private static final String TYPE_AUDIO = "audio";

    public static final String[] IMAGE_EXTENSION = {"jpg", "jpeg", "png", "svg", "bmp", "tiff"};
    public static final String[] AUDIO_EXTENSION = {"mp3", "wav", "wma", "aac", "flac"};
    public static final String[] VIDEO_EXTENSION = {"mp4", "flv", "avi", "wmv", "mpeg", "mov", "rm", "swf"};
    public static final String[] TEXT_EXTENSION = {"txt"};

    private static final String DEFAULT_ROOT_PATH = "/storage/emulated/0";//默认初始位置

    private Context context;

    private TextView tvCurPath, tvConfirm, tvResult;
    private LinearLayout layoutRoot, layoutLast;
    private ListView listView;
    private ListView_FileSelector_Adapter adapter;

    private String curPath;//当前路径
    private List<File> files;//返回值
    private List<Integer> index;//选中文件、夹索引

    private int flag = FLAG_FILE;//默认选择文件
    private String rootPath = DEFAULT_ROOT_PATH;
    private String fileType = TYPE_ALL;

    public FileSelectorDialog(@NonNull Context context) {
        //自定义弹框样式，可使用默认值：0
        this(context, R.style.File_Dialog);
    }

    public FileSelectorDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setCancelable(false);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        View view = mLayoutInflater.inflate(R.layout.layout_file_selector, null);

        tvCurPath = view.findViewById(R.id.tvCurPath);
        tvConfirm = view.findViewById(R.id.tvConfirmChoose);
        tvResult = view.findViewById(R.id.tvResult);

        layoutRoot = view.findViewById(R.id.layoutToRoot);
        layoutLast = view.findViewById(R.id.layoutToLast);

        listView = view.findViewById(R.id.listViewFile);
        index = new ArrayList<>();

        setContentView(view);
        setListener();
        setLayout();
    }

    private void setLayout() {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         */

//        lp.width = 300; // 宽度
        lp.height = 500; // 高度
        lp.alpha = 0.7f; // 透明度

        dialogWindow.setAttributes(lp);
    }

    private void setData() {
        curPath = rootPath;
        tvCurPath.setText(curPath);
        files  = new ArrayList(getFiles(curPath));

        adapter = new ListView_FileSelector_Adapter(context, files, this);//指向的是最开始的list
        listView.setAdapter(adapter);
    }

    private void resetData(String path){
        curPath = path;
        tvCurPath.setText(curPath);
        files.clear();
        index.clear();
//        File[] fs = new File(curPath).listFiles();
//        Arrays.sort(fs, new Comparator<File>() {
//            @Override
//            public int compare(File o1, File o2) {
//                return o1.getName().compareTo(o2.getName());
//            }
//        });

        List<File> temp = getFiles(curPath);

        for(File f : temp){
            files.add(f);
        }
        adapter.notifyDataSetChanged();
    }

    //根据条件筛选显示文件
    private List<File> getFiles(String path){
        //todo 增加排序功能
        if(flag==FLAG_DIR){
            FileFilter filter = File::isDirectory;
            return Arrays.asList(new File(path).listFiles(filter));
        }else if(flag==FLAG_FILE){//显示所有文件夹及选择的类型的文件
            switch (fileType){
                case TYPE_ALL:
                    return Arrays.asList(new File(path).listFiles());
                case TYPE_AUDIO:
                    return getCertainFile(path, AUDIO_EXTENSION);
                case TYPE_IMAGE:
                    return getCertainFile(path, IMAGE_EXTENSION);
                case TYPE_TEXT:
                    return getCertainFile(path, TEXT_EXTENSION);
                case TYPE_VIDEO:
                    return getCertainFile(path, VIDEO_EXTENSION);
            }
        }
        return new ArrayList<>();
    }

    private List<File> getCertainFile(String path, String[] extension){
        List<File> list = new ArrayList<>();
        File[] files = new File(path).listFiles();;
        List extList = Arrays.asList(extension);
        for (File file:files){
            if (file.isDirectory()){
                list.add(file);
                continue;
            }
            if (extList.contains(getExtensionName(file.getName()))){
                list.add(file);
            }
        }
        return list;
    }

    private String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return "";
    }

    //===============================================外部调用api=====================================
    @Override
    public void show() {
        super.show();
        setData();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    //设置根目录
    public void setRootPath(String rootPath) {
        File file = new File(rootPath);
        if(file.exists() && file.isDirectory()){
            this.rootPath = rootPath;
        }
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.cancel();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void itemClick(View v, boolean isChecked) {
        Integer position = (Integer) v.getTag();
        switch (v.getId()){
            case R.id.cbChoose:
                if(isChecked){
//                    SystemUtil.showToast(context, "choose " + (position+1));
                    index.add(position);
                }else{
                    index.remove(position);
                }
                break;
        }
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
                        dismiss();
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
                    SystemUtil.showToast(context, "已是根目录");
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
                    SystemUtil.showToast(context, "已是根目录");
                }else{
                    //back to root
                    resetData(rootPath);
                }
            }
        });
    }

    //自定义回调接口：
    public interface SelectFileCallback{
        void selectResult(List<File> list);
    }
}
