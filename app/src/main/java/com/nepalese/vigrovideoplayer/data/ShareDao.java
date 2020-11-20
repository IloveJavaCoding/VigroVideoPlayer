package com.nepalese.vigrovideoplayer.data;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.nepalese.virgosdk.Util.SPUtil;

import java.util.Set;

/**
 * @author nepalese on 2020/10/29 09:45
 * @usage
 */
public class ShareDao {
    private static final String CONFIG_FILE_NAME = "main_config";
    public static final String KEY_VIDEO_PATH = "video_path";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Set<String> getVideoPath(Context context){
        return SPUtil.getStringSet(context, CONFIG_FILE_NAME, KEY_VIDEO_PATH);
    }

    public static void saveVideoPath(Context context, Set<String> values){
        SPUtil.setStringSet(context, CONFIG_FILE_NAME, KEY_VIDEO_PATH, values);
    }
}
