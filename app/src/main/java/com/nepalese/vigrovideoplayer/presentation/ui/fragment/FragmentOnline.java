package com.nepalese.vigrovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.presentation.component.FileSelectorDialog;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentOnline extends Fragment implements FileSelectorDialog.SelectFileCallback {
    private static final String TAG = "FragmentOnline";

    private View rootView;
    private Context context;

    private Button button;
    private TextView textView;
    private FileSelectorDialog dialog;

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
        button = rootView.findViewById(R.id.button);
        textView = rootView.findViewById(R.id.textView);
        dialog = new FileSelectorDialog(context);
    }

    private void setData() {
        dialog.setFlag(FileSelectorDialog.FLAG_DIR);//设置要选择的是文件还是文件夹
        dialog.setFileType(FileSelectorDialog.TYPE_ALL);//选择dir后，无效
//        dialog.setRootPath("/storage/emulated/0");//设置根目录，若无效调用默认值
        dialog.setCallback(this);//设置回调，必选，若要返回值
    }

    private void setListener() {
        button.setOnClickListener((view)->{
            dialog.show();
        });
    }

    //从文件选择框返回的数据
    @Override
    public void onResult(List<File> list) {
        Log.i(TAG, "onResult: ");
        if(list!=null && list.size()>0){
            StringBuilder builder = new StringBuilder();
            for(File f: list){
                builder.append(f.getAbsolutePath() + "\n");
            }
            textView.setText(builder.toString());
        }
    }
}
