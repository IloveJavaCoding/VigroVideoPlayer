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
    public static final String RE_FILTER_URL = "(https?:)?//(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_?&=#%:]{0}(jpg|png|jpeg|mp3|mp4){1}";

    public static final String[] IMAGE_EXTENSION = {"jpg", "jpeg", "png", "svg", "bmp", "tiff"};
    public static final String[] AUDIO_EXTENSION = {"mp3", "wav", "wma", "aac", "flac"};
    public static final String[] VIDEO_EXTENSION = {"mp4", "flv", "avi", "wmv", "mpeg", "mov", "rm", "swf"};
    public static final String[] TEXT_EXTENSION = {"txt", "java", "html", "xml", "php"};
}
