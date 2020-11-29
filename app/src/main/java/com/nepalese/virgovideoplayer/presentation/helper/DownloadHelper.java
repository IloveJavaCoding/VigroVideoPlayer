package com.nepalese.virgovideoplayer.presentation.helper;

import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.nepalese.virgosdk.Util.ConvertUtil;
import com.nepalese.virgosdk.Util.DateUtil;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgovideoplayer.data.bean.DownloadItem;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/11/25 13:47
 * @usage
 */
public class DownloadHelper {
    private static final String TAG = "DownloadHelper";

    public static List<DownloadTask> getAllTask() {
        //恢复数据
        return OkDownload.restore(DownloadManager.getInstance().getAll());
    }

    public static List<Progress> getAllProgress(){
        return DownloadManager.getInstance().getAll();
    }

    public static void startAllTask() {
        OkDownload.getInstance().startAll();
    }

    public static void clearAllTask(){
        OkDownload.getInstance().removeAll();
    }

    public static void download(DownloadItem downloadItem){
        download(downloadItem.getUrl(), downloadItem.getFileName(), downloadItem.getSavePath());
    }

    public static void download(String url, String fileName, String savePath) {
        //任务存在，继续下载
        if (OkDownload.getInstance().hasTask(url)) {
            DownloadTask task = OkDownload.getInstance().getTask(url);
            if (task.progress.status != Progress.FINISH) {
                Log.i(TAG, "继续下载 = " + url);
                task.start();
            }
            return;
        }

        //新任务
        File file = new File(savePath+File.separator+fileName);
        if(file.exists()) {
            Log.i(TAG, "文件已存在！");
            //以当下时间重命名文件
            fileName = ConvertUtil.string2Hex(String.valueOf(DateUtil.getCurTime2())) + FileUtil.getFileSuffix(fileName);
        }

        GetRequest<File> request = OkGo.<File>get(url);//
        // 同时下载量最大5个，核心3个，
        //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag
        OkDownload.request(url, request)//
                .folder(savePath) // 存储的文件夹
                .fileName(fileName)
                .priority(1)// 优先级
                .save()//
                //.register(new DownloadHelper.DownloadCallback(tag))
                .start();
    }

    private static class DownloadCallback extends DownloadListener {
        private int lastRate = 0;
        private long time;

        DownloadCallback(Object tag) {
            super(tag);
        }

        @Override
        public void onStart(Progress progress) {
            lastRate = 0;
            float rate = progress.currentSize * 1f / progress.totalSize;
            int count = (int) (rate * 100);
            Log.i(TAG, "初始进度 = " + count);
            Log.i(TAG, "开始下载 " + progress.url + " 时间 " + (time = System.currentTimeMillis()));
        }

        @Override
        public void onProgress(Progress progress) {
            float rate = progress.currentSize * 1f / progress.totalSize;
            int count = (int) (rate * 100);
            if (count != lastRate) {
                lastRate = count;
                Log.i(TAG, "进度 = " + lastRate + " progress = " + progress.toString());
            }
        }

        @Override
        public void onError(Progress progress) {
            Throwable throwable = progress.exception;
            if (throwable != null) throwable.printStackTrace();
            Log.e(TAG, tag + " 下载失败 url = " + progress.url);

            DownloadTask task = OkDownload.getInstance().removeTask(progress.tag);
            task.unRegister(this);
        }

        @Override
        public void onFinish(File file, Progress progress) {
            long t = System.currentTimeMillis();
            Log.e(TAG, tag + " 下载完成 = " + file.getAbsolutePath()
                    + "\r\n url = " + progress.url + "\r\n 结束下载时间 " + t + " 总时间 = " + ((t - time)));
        }

        @Override
        public void onRemove(Progress progress) {

        }
    }
}
