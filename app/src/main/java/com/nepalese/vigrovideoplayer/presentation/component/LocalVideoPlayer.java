package com.nepalese.vigrovideoplayer.presentation.component;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.nepalese.vigrovideoplayer.data.bean.Video;
import com.nepalese.virgosdk.VirgoView.VideoView.VirgoVideoViewSurface;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/11/20 11:11
 * @usage
 */
public class LocalVideoPlayer extends VirgoVideoViewSurface {
    private static final String TAG = "VideoPlayer";

    private Context context;

    private List<Video> url = null;
    private int currentIndex = 0;
    private boolean hasSetUrl = false;
    private boolean hasPause = false;
    private boolean isLoop = true; //列表内循环；

    public LocalVideoPlayer(Context context) {
        this(context, null);
    }

    public LocalVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        setLooping(false);

        setCompletionListener(mediaPlayer -> {
            Log.d(TAG, "onCompletion: complete");
            if(isLoop){
                load();
            }
        });

        setErrorListener((mediaPlayer, i, i1) -> {
            if(isLoop){
                load();
            }
            return true;
        });
    }

    public LocalVideoPlayer setUrl(List<Video> urls, int currentIndex) {
        if (urls != null && !urls.isEmpty()) {
            this.url = urls;
            this.hasSetUrl = true;
            this.currentIndex = currentIndex;
        }
        return this;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public void play() {
        if (!hasSetUrl || url == null || url.isEmpty()) {
            return;
        }

        load();
        hasPause = false;
    }

    public void mutePlayer(boolean on){
        setMute(on);
    }

    public void pause(){
        stopPlay();
    }

    public void continuePlayer(){
        continuePlay();
    }

    public void nextOne(){
        load();
    }

    public void lastOne(){
        currentIndex-=2;
        load();
    }

    private void load() {
        if (url == null || url.isEmpty()) return;
        if (currentIndex >= url.size() || currentIndex<0) {
            currentIndex = 0;
        }

        String path = url.get(currentIndex).getPath();
        File file = new File(path);

        if (file.exists()) {
            Log.i(TAG, "播放本地视频: " + path);
            setVideoUri(Uri.fromFile(file));
        } else {
            Log.i(TAG, "播放在线视频");
            setVideoPath(path);
        }
        start();
        currentIndex++;
    }

    //==============================================================================================
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlay();
        clearFocus();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE && isPlaying()) {
            hasPause = true;
            pause();
        } else {
            if (hasPause) {
                start();
            }
        }
    }
}
