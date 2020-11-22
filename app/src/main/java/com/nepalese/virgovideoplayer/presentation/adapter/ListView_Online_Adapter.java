package com.nepalese.virgovideoplayer.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nepalese.virgovideoplayer.R;
import com.nepalese.virgovideoplayer.data.bean.LiveSource;

import java.util.List;

/**
 * @author nepalese on 2020/11/20 18:13
 * @usage
 */
public class ListView_Online_Adapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<LiveSource> data;

    public ListView_Online_Adapter(Context context, List<LiveSource> data) {
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
        public TextView tvName;
        public ImageView imgLogo;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.layout_list_view_online, null);
            viewHolder = new ViewHolder();

            viewHolder.tvName = view.findViewById(R.id.tvLiveName);
            viewHolder.imgLogo = view.findViewById(R.id.imgLogo);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvName.setText(data.get(i).getName());
        //Glide.with(context).load(data.get(i).getThumbPath()).into(viewHolder.imgThumb);
        return view;
    }
}
