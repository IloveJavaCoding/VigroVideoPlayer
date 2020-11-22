package com.nepalese.virgovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.DBHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentSetting extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentSetting";

    private View rootView;
    private Context context;
    private DBHelper dbHelper;

    private Button bClear;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        context = getContext();

        init();
        setData();
        setListener();

        return rootView;
    }

    private void init() {
        dbHelper = DBHelper.getInstance(context);

        bClear = rootView.findViewById(R.id.bClear);
    }

    private void setData() {
    }

    private void setListener() {
        bClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bClear:
                clearAllTable();
                break;
        }
    }

    private void clearAllTable() {
        dbHelper.clearLiveSource();
    }
}
