package com.nepalese.vigrovideoplayer.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
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

/**
 * @author nepalese on 2020/10/29 12:01
 * @usage
 */
public class FragmentOnline extends Fragment {
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
    }

    private void setListener() {
        button.setOnClickListener((view)->{

            dialog.show();
        });
    }
}
