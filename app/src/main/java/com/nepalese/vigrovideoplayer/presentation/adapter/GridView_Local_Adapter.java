package com.nepalese.vigrovideoplayer.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nepalese.vigrovideoplayer.R;
import com.nepalese.vigrovideoplayer.data.bean.Video;
import com.nepalese.virgosdk.Util.MediaUtil;

import java.util.List;

/**
 * @author nepalese on 2020/10/29 17:54
 * @usage
 */
public class GridView_Local_Adapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Video> data;

    public GridView_Local_Adapter(Context context, List<Video> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data==null ? 0:data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder {
        public LinearLayout layout;
        public TextView tvName;
        public ImageView imgThumb;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.layout_grid_view_video, null);
            viewHolder = new ViewHolder();

            viewHolder.layout = view.findViewById(R.id.layout_video_all);
            viewHolder.tvName = view.findViewById(R.id.tvVideoName);
            viewHolder.imgThumb = view.findViewById(R.id.imgVideo);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String path = data.get(i).getPath();
        viewHolder.tvName.setText(data.get(i).getName());
        viewHolder.imgThumb.setImageBitmap(MediaUtil.getVideoThumb(context, path, 1));//file

        return view;
    }
}
