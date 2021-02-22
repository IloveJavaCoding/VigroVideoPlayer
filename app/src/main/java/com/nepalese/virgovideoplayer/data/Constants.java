package com.nepalese.virgovideoplayer.data;

/**
 * @author nepalese on 2020/10/27 16:01
 * @usage
 */
public class Constants {
    //action
    public static final String ACTION_START_HOME = "com.nepalese.vigrovideoplayer.start_home";
    public static final String ACTION_SCAN_VIDEO = "com.nepalese.vigrovideoplayer.scan_video";

    public static final String DIR_THUMB_NAIL = "thumb";//用来存放视频缩略图
    public static final String DIR_DOWNLOAD = "download";
    public static final String DIR_M3U8 = "m3u8";
    public static final String RE_FILTER_URL = "(https?:)?//(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_?&=#%:]{0}(jpg|png|jpeg|mp3|mp4){1}";
    public static final String RE_FILTER_URL_IMG ="(?<=img src=\")[^\"]*(?=\")";
}
