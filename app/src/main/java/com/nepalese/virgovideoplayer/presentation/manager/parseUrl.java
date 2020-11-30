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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nepalese on 2020/11/26 10:47
 * @usage 解析网页链接
 */
public class parseUrl {
    private static final String TAG = "parseUrl";

    private static volatile parseUrl instance;
    private Context context;

    private final String downloadPath;//下载文件放置位置
    private String head = null;//链接头
    private String scheme = null;//协议
    private String html = null;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final long TIME_OUT = 60 * 1000L;

    private parseUrl(Context context){
        this.context = context;
        downloadPath = FileUtil.getAppRootPth(context) + File.separator + Constants.DIR_DOWNLOAD;
//        downloadPath = FileUtil.getRootPath() + File.separator + Constants.DIR_DOWNLOAD;
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

    public List<DownloadItem> getDownloadItemList(String url){
        if(TextUtils.isEmpty(url)) return null;
        head = PathUtil.getUrlHead(url);
        scheme = PathUtil.getUrlScheme(url);

        List<DownloadItem> list = new ArrayList<>();
        int type = PathUtil.getUrlType(url);
        switch (type){
            case PathUtil.TYPE_FILE:
                list.add(getDownloadItem(url));
                break;
            case PathUtil.TYPE_WEB:
//                list.addAll(getDownloadItems(url));
                list.addAll(getDownloadItemsImg(url));
                break;
            case PathUtil.TYPE_OTHER:
                break;
        }
        return list;
    }

    private DownloadItem getDownloadItem(String url){
        DownloadItem downloadItem = new DownloadItem();
        downloadItem.setUrl(url);
        downloadItem.setSavePath(downloadPath);
        downloadItem.setFileName(PathUtil.getNameWithSuffix(url));
        return downloadItem;
    }

    private List<DownloadItem> getDownloadItems(String url){
        getHtml(url);
        executor.shutdown();
        try {
            executor.awaitTermination(TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private List<DownloadItem> getDownloadItemsImg(String url){
        getHtml(url);
        executor.shutdown();
        try {
            executor.awaitTermination(TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<DownloadItem> downloadItems = new ArrayList<>();
        List<String> links = crawlImage(html);
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
                Log.i(TAG, "getHtml: " + html);
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

    private List<String> crawlImage(String html){
        if(!TextUtils.isEmpty(html)){
            //<img src="https://qqpublic.qpic.cn/qq_public/0/0-1505819196-A8F81F3C5693B38EECE559AB83D9E423/600?fmt=jpg&amp;h=1266&amp;ppv=1&amp;size=123&amp;w=600" alt="">
            Pattern pattern = Pattern.compile(Constants.RE_FILTER_URL_IMG);
            Matcher matcher = pattern.matcher(html);
            List<String> list = new ArrayList<>();
            while (matcher.find()) {
                String link = matcher.group();
                if(link.contains("src")){
                    continue;
                }
                if(link.contains("?")){
                    link = link.substring(0, link.indexOf("?")) + ".jpg";
                }
                Log.i(TAG, "crawlImage: link " + link);
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
            return (List<String>) list;
        }
        else{
            Log.e(TAG, "crawlWebPage: 网页内容为空");
        }
        return null;
    }
}
