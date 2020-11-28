package com.nepalese.virgovideoplayer.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nepalese.virgosdk.Util.ConvertUtil;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.bean.Video;

import java.util.List;

/**
 * @author nepalese on 2020/11/24 12:15
 * @usage
 */
public class ListView_VideoList_Adapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Video> data;

    public ListView_VideoList_Adapter(Context context, List<Video> data) {
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
        public TextView tvName, tvDuration, tvSize, tvPath, tvResolution;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.layout_list_view_video, null);
            viewHolder = new ViewHolder();

            viewHolder.imgThumb = view.findViewById(R.id.imgVideo);
            viewHolder.tvName = view.findViewById(R.id.tvVideoName);
            viewHolder.tvDuration = view.findViewById(R.id.tvVideoDuration);
            viewHolder.tvSize = view.findViewById(R.id.tvVideoSize);
            viewHolder.tvPath = view.findViewById(R.id.tvVideoPath);
            viewHolder.tvResolution = view.findViewById(R.id.tvVideoResolution);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Video video = data.get(i);
        viewHolder.tvName.setText(video.getName());
        viewHolder.tvDuration.setText(ConvertUtil.formatTime(video.getDuration()));
        viewHolder.tvSize.setText(ConvertUtil.formatFileSize(video.getSize()));
        viewHolder.tvPath.setText(video.getPath());
        viewHolder.tvResolution.setText(video.getResolution());

        Glide.with(context).load(video.getThumbPath()).into(viewHolder.imgThumb);
        return view;
    }
}
