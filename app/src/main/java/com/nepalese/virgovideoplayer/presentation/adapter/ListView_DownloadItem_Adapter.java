package com.nepalese.virgovideoplayer.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.bean.DownloadItem;

import java.util.List;

/**
 * @author nepalese on 2020/11/25 12:00
 * @usage
 */
public class ListView_DownloadItem_Adapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<DownloadItem> data;

    public ListView_DownloadItem_Adapter(Context context, List<DownloadItem> data) {
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
        public TextView tvName, tvUrl;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.layout_list_view_download_item, null);
            viewHolder = new ViewHolder();

            viewHolder.imgThumb = view.findViewById(R.id.imgDownloadThumb);
            viewHolder.tvName = view.findViewById(R.id.tvDownloadName);
            viewHolder.tvUrl = view.findViewById(R.id.tvDownloadUrl);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvName.setText(data.get(i).getFileName());
        viewHolder.tvUrl.setText(data.get(i).getUrl());

        String url = data.get(i).getUrl();
        if(url.endsWith("jpg") || url.endsWith("png") || url.endsWith("jpeg")){
            Glide.with(context).load(url).into(viewHolder.imgThumb);
        }else{
            viewHolder.imgThumb.setImageResource(R.mipmap.ic_launcher);
        }

        return view;
    }
}
