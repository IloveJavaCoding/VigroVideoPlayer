package com.nepalese.virgovideoplayer.presentation.manager;/*
 *  Nepalese created on 2020/11/29
 * usage:
 */

import android.text.TextUtils;

public class PathUtil {
    //文件路径：
    public static final String[] IMAGE_EXTENSION = {"jpg", "jpeg", "png", "svg", "bmp", "tiff"};
    public static final String[] AUDIO_EXTENSION = {"mp3", "wav", "wma", "aac", "flac"};
    public static final String[] VIDEO_EXTENSION = {"mp4", "flv", "avi", "wmv", "mpeg", "mov", "rm", "swf"};
    public static final String[] TEXT_EXTENSION = {"txt", "java", "html", "xml", "php"};

    //链接：
    private static final String[] SUFFIX_FILE = {"mp3", "jpg", "png", "jpeg", "mp4", "m3u8", "pdf", "txt"};
    private static final String[] SUFFIX_WEB = {"html", "net", "com", "cn"};

    //Intent 跳转文件类型
    private static final String INTENT_TYPE_IMAGE = "image/*";
    private static final String INTENT_TYPE_AUDIO = "audio/*";
    private static final String INTENT_TYPE_VIDEO = "video/*";
    private static final String INTENT_TYPE_TEXT = "text/plain";
    private static final String INTENT_TYPE_ALL = "*/*";

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_TEXT = 4;
    public static final int TYPE_FILE = 5;//可下载文件
    public static final int TYPE_WEB = 6;//网页
    public static final int TYPE_OTHER = 8;


    //获取文件或链接的后缀名(.mp3 ...)
    public static String getFileSuffix(String path){
        if(TextUtils.isEmpty(path)) return null;

        if(path.contains(".")){
            return path.substring(path.lastIndexOf("."));
        }

        return null;
    }

    //获取文件名，不带后缀名
    public static String getFileName(String url) {
        return TextUtils.isEmpty(url) ? null : url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
    }

    //获取带有后缀名的文件名
    public static String getNameWithSuffix(String url) {
        return TextUtils.isEmpty(url) ? null : url.substring(url.lastIndexOf("/") + 1);
    }

    //获取整个链接头：协议及主机地址 http(s)://xxx.xxx.xxx/
    public static String getUrlHead(String url){
        if(TextUtils.isEmpty(url)) return null;

        if(!(url.startsWith("http")||url.startsWith("https")))return null;

        return url.substring(0, url.indexOf("/", 10));
    }

    //获取url链接协议类型：http、https...
    public static String getUrlScheme(String url){
        if(TextUtils.isEmpty(url)) return null;

        if(!(url.startsWith("http")||url.startsWith("https")))return null;

        return url.substring(0, url.indexOf("/"));
    }

    //获取文件类型
    public static int getFileType(String filePath){
        String suffix = getFileSuffix(filePath);
        if(suffix!=null){
            suffix = suffix.substring(1);
            for(String str: IMAGE_EXTENSION){
               if(str.equals(suffix)){
                   return TYPE_IMAGE;
               }
            }

            for(String str: AUDIO_EXTENSION){
                if(str.equals(suffix)){
                    return TYPE_AUDIO;
                }
            }

            for(String str: VIDEO_EXTENSION){
                if(str.equals(suffix)){
                    return TYPE_VIDEO;
                }
            }

            for(String str: TEXT_EXTENSION){
                if(str.equals(suffix)){
                    return TYPE_TEXT;
                }
            }
        }

        return TYPE_OTHER;
    }

    //判断链接类型
    public static int getUrlType(String url){
        if(TextUtils.isEmpty(url) || !(url.startsWith("http") || url.startsWith("https"))){
            return TYPE_OTHER;
        }

        for(String str: SUFFIX_FILE){
            if(url.endsWith(str)){
                return TYPE_FILE;
            }
        }

        return TYPE_WEB;
    }

    public static String getIntentType(String filePath){
        int fileType = getFileType(filePath);
        switch (fileType){
            case TYPE_IMAGE:
                return INTENT_TYPE_IMAGE;
            case TYPE_AUDIO:
                return INTENT_TYPE_AUDIO;
            case TYPE_VIDEO:
                return INTENT_TYPE_VIDEO;
            case TYPE_TEXT:
                return INTENT_TYPE_TEXT;
            default:
                return INTENT_TYPE_ALL;
        }
    }
}
