package com.nepalese.virgovideoplayer.presentation.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgovideoplayer.data.Constants;
import com.nepalese.virgovideoplayer.data.bean.DownloadItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nepalese on 2020/11/26 10:47
 * @usage 解析网页链接
 */
public class parseUrl {
    private static final String TAG = "parseUrl";

    private final int TYPE_UNKNOWN = -1;
    private final int TYPE_FILE = 1;
    private final int TYPE_WEB = 2;
    //可下载文件：
    private static final String[] SUFFIX_FILE = {"mp3", "jpg", "png", "jpeg", "mp4", "m3u8", "pdf", "txt"};
    //网页
    private static final String[] SUFFIX_WEB = {"html", "net", "com", "cn"};

    private static volatile parseUrl instance;
    private Context context;
    private String downloadPath;//下载文件放置位置
    private String head = null;//链接头
    private String scheme = null;//协议
    private String html = null;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    private parseUrl(Context context){
        this.context = context;
        downloadPath = FileUtil.getAppRootPth(context) + File.separator + Constants.DIR_DOWNLOAD;
    }
    public static parseUrl getInstance(Context context){
        if(instance==null){
            synchronized (parseUrl.class){
                if(instance==null){
                    instance = new parseUrl(context);
                }
            }
        }
        return instance;
    }

    private int getUrlType(String url){
        if(TextUtils.isEmpty(url) || !(url.startsWith("http") || url.startsWith("https"))){
            return TYPE_UNKNOWN;
        }

        for(String str: SUFFIX_FILE){
            if(url.endsWith(str)){
                return TYPE_FILE;
            }
        }

        for(String str: SUFFIX_WEB){
            if(url.endsWith(str)){
                return TYPE_WEB;
            }
        }

        return TYPE_UNKNOWN;
    }

    public List<DownloadItem> getDownloadItemList(String url){
        if(TextUtils.isEmpty(url)) return null;
        head = getUrlHead(url);
        scheme = getUrlScheme(url);

        List<DownloadItem> list = new ArrayList<>();
        int type = getUrlType(url);
        switch (type){
            case TYPE_FILE:
                list.add(getDownloadItem(url));
                break;
            case TYPE_WEB:
                list.addAll(getDownloadItems(url));
                break;
            case TYPE_UNKNOWN:
                break;
        }
        return list;
    }

    private DownloadItem getDownloadItem(String url){
        DownloadItem downloadItem = new DownloadItem();
        downloadItem.setUrl(url);
        downloadItem.setSavePath(downloadPath);
        downloadItem.setFileName(FileUtil.getNameWithSuffix4Url(url));
        return downloadItem;
    }

    private List<DownloadItem> getDownloadItems(String url){
        getHtml(url);
        executor.shutdown();
        Log.i(TAG, "等待下载中...");
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<DownloadItem> downloadItems = new ArrayList<>();
        List<String> links = crawlWebPage(html);
        if(links!=null){
            for(String link: links){
                downloadItems.add(getDownloadItem(link));
            }
        }else{
            Log.e(TAG, "getDownloadItems: 未找到任何链接");
        }
        return downloadItems;
    }

    private void getHtml(String url){
        executor.submit(() -> {
            StringBuilder buffer = new StringBuilder();
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            try {
                URL url1 = new URL(url);
                URLConnection uc = url1.openConnection();
                uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36)");

                inputStreamReader = new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8);
                bufferedReader = new BufferedReader(inputStreamReader);

                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    buffer.append(temp.trim());
                }
                html = buffer.toString();
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        inputStreamReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private List<String> crawlWebPage(String html) {
        if(!TextUtils.isEmpty(html)) {
            Log.i(TAG, "crawlWebPage: " + html);
            //src="http://pic1.win4000.com/wallpaper/2020-11-24/5fbca616736aa.jpg"
            //src="//img.tukuppt.com/newpreview_music/08/98/81/5c88be672193357468.mp3"
            Pattern pattern = Pattern.compile(Constants.RE_FILTER_URL);
            Matcher matcher = pattern.matcher(html);

            List<String> list = new ArrayList<>();
            while (matcher.find()) {
                String link = matcher.group();
                if(link.contains("html") || link.contains("=")) {
                    continue;
                }
                //1. 仅缺少协议：http(s):
                if (link.startsWith("//")) {
                    link = scheme + link;
                }
                //2. 缺少整个链接头： http(s)://xxx.xxx.com
                else if (link.startsWith("/")) {
                    link = head + link;
                }
                //3. 完整的链接
                list.add(link);
            }
            return list;
        }else{
            Log.e(TAG, "crawlWebPage: 网页内容为空");
        }
        return null;
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
}
