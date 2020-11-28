package com.nepalese.virgovideoplayer.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.nepalese.virgosdk.Util.ConvertUtil;
import com.nepalese.virgosdk.Util.FileUtil;
import com.nepalese.virgovideoplayer.R;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * @author nepalese on 2020/11/25 15:22
 * @usage
 */
public class ListView_DownloadTask_Adapter extends BaseAdapter {
    private static final String TAG_FILE = "file";//用来区分下载的文件类型
    private static final String TAG_AUDIO = "audio";//用来区分下载的文件类型
    private static final String TAG_VIDEO = "video";//用来区分下载的文件类型
    private static final String TAG_IMAGE = "image";//用来区分下载的文件类型

    private Context context;
    private LayoutInflater inflater;
    private List<DownloadTask> data;

    public ListView_DownloadTask_Adapter(Context context, List<DownloadTask> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data==null ? 0:data.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        public ImageView imgThumb;
        public TextView tvName, tvCurSize, tvTotalSize, tvPercent;
        public TextView tvStatue, tvDelete, tvReDo;
        public ProgressBar progressBar;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.layout_list_view_download_task, null);
            viewHolder = new ViewHolder();

            viewHolder.imgThumb = view.findViewById(R.id.imgTaskThumb);
            viewHolder.tvName = view.findViewById(R.id.tvTaskName);
            viewHolder.tvCurSize = view.findViewById(R.id.tvTaskDoneSize);
            viewHolder.tvTotalSize = view.findViewById(R.id.tvTaskTotalSize);

            viewHolder.tvPercent = view.findViewById(R.id.tvTaskPercent);
            viewHolder.progressBar = view.findViewById(R.id.pbDetail);

            viewHolder.tvStatue = view.findViewById(R.id.tvTaskState);
            viewHolder.tvDelete = view.findViewById(R.id.tvTaskDel);
            viewHolder.tvReDo = view.findViewById(R.id.tvTaskRedo);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DownloadTask task = data.get(position);
        String tag = TAG_FILE;
        switch (FileUtil.getFileSuffix(task.progress.fileName)){
            case ".mp3":
            case ".wav":
                tag = TAG_AUDIO;
                break;
            case ".mp4":
                tag = TAG_VIDEO;
                break;
            case ".jpg":
            case ".png":
            case ".jpeg":
                tag = TAG_IMAGE;
                break;
        }
        task.register(new MyDownloadListener(tag, viewHolder));

        final Progress progress = task.progress;
        String url = progress.tag;
        if(url.endsWith("jpg") || url.endsWith("png") || url.endsWith("jpeg")){
            Glide.with(context).load(url).into(viewHolder.imgThumb);
        }else{
            viewHolder.imgThumb.setImageResource(R.mipmap.ic_launcher);
        }

        viewHolder.tvName.setText(progress.fileName);
        viewHolder.tvCurSize.setText(ConvertUtil.formatFileSize(progress.currentSize, 0));
        viewHolder.tvTotalSize.setText(ConvertUtil.formatFileSize(progress.totalSize, 0));
        float rate = progress.currentSize/(progress.totalSize*1.0f) * 100;
        viewHolder.tvPercent.setText(String.format("%.2f", rate, Locale.CANADA)+"%");

        viewHolder.progressBar.setProgress((int) rate);

        setState(progress, viewHolder);
        return view;
    }

    private void setState(Progress progress, ViewHolder viewHolder){
        switch (progress.status){
            case  1:
                viewHolder.tvStatue.setText("等待");
                break;
            case  2:
                viewHolder.tvStatue.setText("下载");
                break;
            case  3:
                viewHolder.tvStatue.setText("暂停");
                break;
            case  4:
                viewHolder.tvStatue.setText("错误");
                break;
            case  5:
                viewHolder.tvStatue.setText("完成");
                break;
        }
    }

    private static class MyDownloadListener extends DownloadListener{
        private ViewHolder viewHolder;

        public MyDownloadListener(Object tag, ViewHolder viewHolder) {
            super(tag);
            this.viewHolder = viewHolder;
        }

        @Override
        public void onStart(Progress progress) {
            viewHolder.tvStatue.setText("等待");
        }

        @Override
        public void onProgress(Progress progress) {
            viewHolder.tvStatue.setText("下载");
            viewHolder.tvCurSize.setText(ConvertUtil.formatFileSize(progress.currentSize));
            viewHolder.tvTotalSize.setText(ConvertUtil.formatFileSize(progress.totalSize));
            float rate = progress.currentSize/(progress.totalSize*1.0f) * 100;
            viewHolder.tvPercent.setText(String.format("%.2f", rate, Locale.CANADA)+"%");
            viewHolder.progressBar.setProgress((int) rate);
        }

        @Override
        public void onError(Progress progress) {
            viewHolder.tvStatue.setText("错误");
            DownloadTask task = OkDownload.getInstance().removeTask(progress.tag);
            task.unRegister(this);
        }

        @Override
        public void onFinish(File file, Progress progress) {
            viewHolder.tvStatue.setText("完成");
        }

        @Override
        public void onRemove(Progress progress) {

        }
    }
}


