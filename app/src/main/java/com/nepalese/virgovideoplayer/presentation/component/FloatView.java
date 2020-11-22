package com.nepalese.virgovideoplayer.presentation.component;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nepalese.virgovideoplayer.R;

/**
 * @author nepalese on 2020/10/29 09:00
 * @usage
 */
public class FloatView extends LinearLayout {
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private Context mCtx;
    private FloatView self;
    private TextView tvContent;
    private ProgressBar progressBar;

    public FloatView(Context context) {
        super(context);
        mCtx = context;
        self = this;
        wm = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        initWindow();
        setOrientation(VERTICAL);
    }

    public void destroy() {
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

    /**
     * 初始化windowManager
     */
    private void initWindow() {
        wmParams = getParams();
        wmParams.gravity = Gravity.CENTER;
        wmParams.x = 50;
        wmParams.y = 50;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mCtx);
        View view = mLayoutInflater.inflate(R.layout.layout_float, null);
        tvContent = view.findViewById(R.id.tvContent);
        progressBar = view.findViewById(R.id.pbLoad);
        self.addView(view);
    }

    public void updateContent(String cont) {
        if (self.getParent() == null) {
            wm.addView(self, wmParams);
        }
        //显示内容
        tvContent.setText(cont);
    }

    public void controlProcessBar(boolean show){
        if (self.getParent() == null) {
            wm.addView(self, wmParams);
        }
        if(show){
            progressBar.setVisibility(VISIBLE);
        }else {
            progressBar.setVisibility(GONE);
        }
    }

    /**
     * 对windowManager进行设置
     *
     * @return
     */
    private WindowManager.LayoutParams getParams() {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        //wmParams.type = LayoutParams.TYPE_PHONE;//
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//				|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN| WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR|
//				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return wmParams;
    }
}
